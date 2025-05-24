package com.moneysaving.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WARNING: This controller is for testing purposes only and should NOT be used in production
 * as it allows direct SQL execution which is a security risk.
 */
@RestController
@RequestMapping("/api/test")
public class DatabaseQueryController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @PostMapping("/execute-query")
    public ResponseEntity<?> executeQuery(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        Map<String, Object> response = new HashMap<>();
        
        if (query == null || query.trim().isEmpty()) {
            response.put("error", "Query cannot be empty");
            return ResponseEntity.badRequest().body(response);
        }
        
        // For safety, only allow SELECT queries
        if (!query.trim().toLowerCase().startsWith("select")) {
            response.put("error", "Only SELECT queries are allowed for safety reasons");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
            response.put("results", results);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error executing query: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
