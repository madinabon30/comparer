package uz.fb.comparer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MetadataService {
    private final JdbcTemplate jdbcTemplate;
    public List<Map<String, Object>> getViewColumns(String tableName, String columnName) {
        return jdbcTemplate.queryForList("""
                SELECT
                                             column_name,
                                             data_type,
                                             character_maximum_length,
                                             numeric_precision
                                         FROM information_schema.columns
                                         WHERE
                                             --table_schema = 'public' AND
                                             table_name   = ? and column_name = ?
                                         ORDER BY ordinal_position;
            """, tableName, columnName);
    }
}
