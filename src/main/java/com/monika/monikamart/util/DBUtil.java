package com.monika.monikamart.util;

import com.monika.monikamart.exception.DatabaseException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtil {
    private static final Logger LOGGER = Logger.getLogger(DBUtil.class.getName());
    private static HikariDataSource dataSource;
    private static Properties configProperties = new Properties();

    private DBUtil() {}

    public static synchronized void initialize() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        loadConfigProperties();

        try {
            HikariConfig config = new HikariConfig();

            // Load driver and DB URL, preferring environment variables if set
            String driver = getProp("db.driver", "DB_DRIVER", "org.h2.Driver");
            String url = getProp("db.url", "DB_URL", "jdbc:h2:./data/monikamart;DB_CLOSE_DELAY=-1;MODE=MySQL;DATABASE_TO_UPPER=false");
            String user = getProp("db.user", "DB_USER", "sa");
            String password = getProp("db.password", "DB_PASSWORD", "");

            config.setDriverClassName(driver);
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);

            config.setMaximumPoolSize(Integer.parseInt(getProp("db.pool.maximumPoolSize", "DB_POOL_MAX", "15")));
            config.setMinimumIdle(Integer.parseInt(getProp("db.pool.minimumIdle", "DB_POOL_MIN", "5")));
            config.setIdleTimeout(Long.parseLong(getProp("db.pool.idleTimeout", "DB_POOL_IDLE_TIMEOUT", "300000")));
            config.setConnectionTimeout(Long.parseLong(getProp("db.pool.connectionTimeout", "DB_POOL_CONN_TIMEOUT", "20000")));
            config.setMaxLifetime(Long.parseLong(getProp("db.pool.maxLifetime", "DB_POOL_MAX_LIFETIME", "1800000")));
            config.setPoolName("MonikaMartHikariPool");

            dataSource = new HikariDataSource(config);
            LOGGER.info("HikariCP connection pool initialized successfully with URL: " + url);

            // Execute schema migrations and seed data
            applySchemaAndMigrations();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize HikariCP connection pool", e);
            throw new DatabaseException("Could not initialize connection pool: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            initialize();
        }
        return dataSource.getConnection();
    }

    public static synchronized void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            LOGGER.info("Shutting down HikariCP connection pool...");
            dataSource.close();
            dataSource = null;
        }
    }

    public static Properties getConfigProperties() {
        if (configProperties.isEmpty()) {
            loadConfigProperties();
        }
        return configProperties;
    }

    public static String getProperty(String key, String defaultValue) {
        if (configProperties.isEmpty()) {
            loadConfigProperties();
        }
        String envKey = key.toUpperCase().replace('.', '_');
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal;
        }
        return configProperties.getProperty(key, defaultValue);
    }

    private static void loadConfigProperties() {
        try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                configProperties.load(is);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "config.properties not found in classpath, using defaults", e);
        }
    }

    private static String getProp(String propKey, String envKey, String defaultVal) {
        String env = System.getenv(envKey);
        if (env != null && !env.trim().isEmpty()) {
            return env;
        }
        return configProperties.getProperty(propKey, defaultVal);
    }

    public static void applySchemaAndMigrations() {
        try (Connection conn = getConnection()) {
            executeSqlScript(conn, "db/schema.sql");
            executeSqlScript(conn, "db/migration/V2__order_status_workflow.sql");
            seedInitialDataIfEmpty(conn);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error applying schema or seed scripts", e);
        }
    }

    public static void executeSqlScript(Connection conn, String resourcePath) {
        try (InputStream is = DBUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                LOGGER.warning("SQL resource script not found: " + resourcePath);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("--") || trimmed.isEmpty()) {
                        continue;
                    }
                    sql.append(line).append("\n");
                    if (trimmed.endsWith(";")) {
                        String statementStr = sql.toString().replace(";", "").trim();
                        if (!statementStr.isEmpty()) {
                            try (Statement stmt = conn.createStatement()) {
                                stmt.execute(statementStr);
                            } catch (SQLException ex) {
                                // Ignore IF NOT EXISTS or already existing column warnings
                                if (!ex.getMessage().toLowerCase().contains("already exists") &&
                                    !ex.getMessage().toLowerCase().contains("duplicate")) {
                                    LOGGER.warning("Notice on executing SQL: " + ex.getMessage());
                                }
                            }
                        }
                        sql.setLength(0);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed executing script: " + resourcePath, e);
        }
    }

    private static void seedInitialDataIfEmpty(Connection conn) {
        try {
            boolean hasUsers = false;
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasUsers = true;
                }
            }

            if (!hasUsers) {
                LOGGER.info("Users table is empty. Executing seed script and hashing passwords...");
                // Insert default admin, sellers, buyers with fresh BCrypt hashes
                String adminHash = PasswordUtil.hashPassword("Admin@123");
                String sellerHash = PasswordUtil.hashPassword("Seller@123");
                String buyerHash = PasswordUtil.hashPassword("Buyer@123");

                String insertUser = "INSERT INTO users (id, name, email, password_hash, role, phone, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
                    // 1. Admin
                    ps.setInt(1, 1);
                    ps.setString(2, "System Administrator");
                    ps.setString(3, "admin@monikamart.com");
                    ps.setString(4, adminHash);
                    ps.setString(5, "ADMIN");
                    ps.setString(6, "+91 9876543210");
                    ps.setString(7, "Anna University Campus, Chennai, Tamil Nadu");
                    ps.addBatch();

                    // 2. Seller 1
                    ps.setInt(1, 2);
                    ps.setString(2, "Aditya Electronics");
                    ps.setString(3, "seller1@monikamart.com");
                    ps.setString(4, sellerHash);
                    ps.setString(5, "SELLER");
                    ps.setString(6, "+91 9876543211");
                    ps.setString(7, "T. Nagar Commercial Complex, Chennai, Tamil Nadu");
                    ps.addBatch();

                    // 3. Seller 2
                    ps.setInt(1, 3);
                    ps.setString(2, "Priya BookStore & Stationery");
                    ps.setString(3, "seller2@monikamart.com");
                    ps.setString(4, sellerHash);
                    ps.setString(5, "SELLER");
                    ps.setString(6, "+91 9876543212");
                    ps.setString(7, "Cross Cut Road, Gandhipuram, Coimbatore, Tamil Nadu");
                    ps.addBatch();

                    // 4. Buyer 1
                    ps.setInt(1, 4);
                    ps.setString(2, "Monika S.");
                    ps.setString(3, "buyer1@monikamart.com");
                    ps.setString(4, buyerHash);
                    ps.setString(5, "BUYER");
                    ps.setString(6, "+91 9876543213");
                    ps.setString(7, "42 Tech Avenue, Guindy, Chennai, Tamil Nadu");
                    ps.addBatch();

                    // 5. Buyer 2
                    ps.setInt(1, 5);
                    ps.setString(2, "Karthik Raman");
                    ps.setString(3, "buyer2@monikamart.com");
                    ps.setString(4, buyerHash);
                    ps.setString(5, "BUYER");
                    ps.setString(6, "+91 9876543214");
                    ps.setString(7, "15 Green Park Road, Madurai, Tamil Nadu");
                    ps.addBatch();

                    ps.executeBatch();
                }

                // Execute the rest of seed products & sample orders
                executeSqlScript(conn, "db/seed.sql");
                LOGGER.info("Seeding completed successfully.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error during seed checking/execution: " + e.getMessage());
        }
    }
}
