package uz.fb.comparer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MetadataService {
    private final JdbcTemplate jdbcTemplate;

    public MetadataService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getViewColumns(String tableName, String columnName) {
        return jdbcTemplate.queryForList("""
                SELECT
                    COLUMN_NAME,
                    DATA_TYPE,
                    DATA_LENGTH
                    FROM ALL_TAB_COLUMNS
                    WHERE
                      OWNER = 'IBS' 
                      AND TABLE_NAME   = ? 
                      and COLUMN_NAME = ?
                ORDER BY COLUMN_ID;
            """, tableName, columnName);
    }
}
