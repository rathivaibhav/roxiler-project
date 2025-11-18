package tech.gurugram.rating_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminSqlController {

    @Autowired
    private JdbcTemplate jdbc;
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sql")
    public ResponseEntity<?> runSql(@RequestBody Map<String,String> body) {
        String sql = body.get("sql");
        if (sql == null) return ResponseEntity.badRequest().body("sql required");
        String s = sql.trim().toLowerCase();
        if (!s.startsWith("select") || s.contains(";") || s.contains("insert ") || s.contains("update ") || s.contains("delete ") || s.contains("drop ")) {
            return ResponseEntity.badRequest().body("Only single SELECT queries without semicolons are allowed");
        }
        try {
            List<Map<String,Object>> rows = jdbc.queryForList(sql);
            return ResponseEntity.ok(rows);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("SQL error: " + ex.getMessage());
        }
    }
}

