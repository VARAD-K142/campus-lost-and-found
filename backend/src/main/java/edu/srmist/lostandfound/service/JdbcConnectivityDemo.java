package edu.srmist.lostandfound.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Demonstrates native JDBC database connectivity, PreparedStatements,
 * and related-record SQL queries for Course Outcome CO-3.
 */
@Service
public class JdbcConnectivityDemo {

    private static final Logger logger = LoggerFactory.getLogger(JdbcConnectivityDemo.class);
    private final DataSource dataSource;

    public JdbcConnectivityDemo(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Executes raw JDBC PreparedStatements against the MySQL database.
     * Demonstrates:
     * 1. Connection retrieval from DataSource pool
     * 2. DatabaseMetaData inspection
     * 3. Parameterized SELECT with INNER JOIN between items and users
     * 4. Safe resource closing via try-with-resources
     */
    public Map<String, Object> runJdbcVerification() {
        Map<String, Object> results = new HashMap<>();

        String sqlJoin = "SELECT i.id, i.title, i.category, i.status, u.full_name AS reporter_name " +
                         "FROM items i " +
                         "INNER JOIN users u ON i.reporter_id = u.id " +
                         "WHERE i.status = ? " +
                         "LIMIT 5";

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            results.put("databaseProductName", metaData.getDatabaseProductName());
            results.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            results.put("driverName", metaData.getDriverName());
            results.put("url", metaData.getURL());

            try (PreparedStatement pstmt = connection.prepareStatement(sqlJoin)) {
                pstmt.setString(1, "ACTIVE");

                try (ResultSet rs = pstmt.executeQuery()) {
                    int rowCount = 0;
                    while (rs.next()) {
                        rowCount++;
                    }
                    results.put("activeItemsFoundViaJdbcJoin", rowCount);
                    results.put("status", "SUCCESS");
                }
            }
        } catch (Exception e) {
            logger.error("JDBC verification failed", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }

        return results;
    }
}
