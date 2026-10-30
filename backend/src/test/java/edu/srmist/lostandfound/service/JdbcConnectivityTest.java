package edu.srmist.lostandfound.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JdbcConnectivityTest {

    @Autowired
    private JdbcConnectivityDemo jdbcConnectivityDemo;

    @Test
    @DisplayName("CO-3 Verification: Should connect via raw JDBC PreparedStatement, inspect metadata, and execute query")
    void shouldExecuteRawJdbcQuerySuccessfully() {
        Map<String, Object> results = jdbcConnectivityDemo.runJdbcVerification();

        assertThat(results).isNotNull();
        assertThat(results.get("status")).isEqualTo("SUCCESS");
        assertThat(results.get("databaseProductName")).asString().containsIgnoringCase("MySQL");
        assertThat(results.get("url")).asString().contains("3307");
        assertThat(results).containsKey("activeItemsFoundViaJdbcJoin");
    }
}
