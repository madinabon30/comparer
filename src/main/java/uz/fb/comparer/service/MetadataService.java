package uz.fb.comparer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uz.fb.comparer.model.ObjectEntity;
import uz.fb.comparer.repository.MetadataRepository;

import java.util.List;
import java.util.Map;

@Service
public class MetadataService {
    private final MetadataRepository metadataRepository;

    public MetadataService(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public List<ObjectEntity> getViewColumns(String tableName, String columnName) {
        return metadataRepository.getMetadataViewColumn(tableName, columnName);
    }
}
