package com.monika.monikamart.dao;

import com.monika.monikamart.util.DBUtil;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseDAOTest {

    @BeforeAll
    public static void setupTestDatabase() {
        // Ensure test environment uses in-memory H2 database
        DBUtil.initialize();
    }
}
