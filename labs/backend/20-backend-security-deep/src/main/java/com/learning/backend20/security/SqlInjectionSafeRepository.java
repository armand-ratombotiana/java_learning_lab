package com.learning.backend20.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class SqlInjectionSafeRepository {

    private static final Logger log = LoggerFactory.getLogger(SqlInjectionSafeRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public SqlInjectionSafeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> findUserByName(String name) {
        String sql = "SELECT * FROM users WHERE name = ?";
        log.debug("Executing safe query: {} with param: {}", sql, name);
        return jdbcTemplate.queryForList(sql, name);
    }

    public List<Map<String, Object>> findUsersByAgeRange(int minAge, int maxAge) {
        String sql = "SELECT * FROM users WHERE age BETWEEN ? AND ?";
        return jdbcTemplate.queryForList(sql, minAge, maxAge);
    }
}
