package uz.fb.comparer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.fb.comparer.model.ObjectEntity;
import uz.fb.comparer.model.RequestDTO;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ComparingService {

    private final MetadataService metadataService;

    private static final Logger log = LoggerFactory.getLogger(ComparingService.class);

    public ComparingService(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    public void checkFilterRequest(RequestDTO request){
        //db dan hashed pnfl list olinadi - temp solution
        PinflFilterMatcher matcher = new PinflFilterMatcher(Set.of("8a9f0ea9d400aa60c214893766adff2deb388889a25089935fc061ee37533142"));

        request.getFilters().forEach(filterDTO -> {
            if(filterDTO.getFilterValues().size()==1) {
                if (PinflFilterMatcher.isValidPinfl(filterDTO.getFilterValues().get(0))){
                    if(validateMetadata(request.getViewName(), filterDTO.getColumnName())) {
                        List<String> matchedPinflList = matcher.extractMatchedPinfl(filterDTO.getFilterValues().get(0));
                        //writing matched pinfl to db
                        log.info("Requested to the sensitive pinfls : {}, Request : {}", matchedPinflList, request);
                    } else
                        log.info("The requested column is not passed metadata validation. Requested view : {}, requested column : {}", request.getViewName(), filterDTO.getColumnName());
                }else
                    log.info("The requested value is not matched with mask of pinfl. The value is : {}", filterDTO.getFilterValues().get(0));

            }else
                log.info("The requested values in range : {}-{}", filterDTO.getFilterValues().get(0), filterDTO.getFilterValues().get(1));

        });
    }




    private boolean validateMetadata(String viewName, String columnName) {
        //if it's like view.column_name
        var ref = new Object() {
            boolean b;
        };

            List<ObjectEntity> viewColumns = metadataService.getViewColumns(viewName, columnName);
            if (!viewColumns.isEmpty()) {
                  viewColumns.forEach(c -> {
                     ref.b = c.getDataType().equals("NUMBER") ? c.getDataPrecision().equals(new BigDecimal(14)) : c.getDataType().equals("VARCHAR2") && c.getDataLength().equals(new BigDecimal(14));
                 });
            }

        //other cases should be dealt
        return ref.b;
    }


}
