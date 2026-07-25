# Stage 1: Build artifact with Maven and OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime with Apache Tomcat 9.0.x (javax.servlet compatible)
FROM tomcat:9.0-jdk17-temurin
LABEL maintainer="Monika Subramanian <monika@monikamart.com>"
LABEL description="MonikaMart E-Commerce Platform - Anna University R2025 Semester 3 Capstone"

# Remove default ROOT application to deploy MonikaMart as root
RUN rm -rf /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/ROOT.war

# Copy war from builder stage
COPY --from=builder /app/target/monikamart.war /usr/local/tomcat/webapps/ROOT.war

# Create persistent data directory for H2 database storage
RUN mkdir -p /usr/local/tomcat/data

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/health || exit 1

CMD ["catalina.sh", "run"]
