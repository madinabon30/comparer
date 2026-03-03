package uz.fb.comparer.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.fb.comparer.model.ObjectEntity;

import java.util.List;

@Repository
public interface MetadataRepository extends CrudRepository<ObjectEntity, Long> {

    @Query("""
             SELECT
                                COLUMN_NAME,
                                DATA_TYPE,
                                DATA_PRECISION,
                                DATA_LENGTH
                                FROM ALL_TAB_COLUMNS
                                WHERE
                                  OWNER = 'IBS' 
                                  AND TABLE_NAME   = :tableName 
                                  and COLUMN_NAME = :columnName
                            ORDER BY COLUMN_ID
            """)
    List<ObjectEntity> getMetadataViewColumn(@Param("tableName") String tableName, @Param("columnName") String columnName);
}
