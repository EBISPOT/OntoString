package uk.ac.ebi.spot.ontotools.curation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.ontotools.curation.constants.CSVImportHeader;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.dataimport.ImportDataElementDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.dataimport.ImportDataPackageDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVDataTransform {

    private static final Logger log = LoggerFactory.getLogger(CSVDataTransform.class);

    private Map<String, Integer> headerIndex;

    private List<ImportDataElementDto> dataElements;

    public CSVDataTransform(List<String[]> data, Map<String, Integer> headerIndex) {
        this.headerIndex = headerIndex;
        dataElements = new ArrayList<>();

        for (int i = 1; i < data.size(); i++) {
            transformLine(i, data.get(i));
        }
    }

    private void transformLine(int lineNo, String[] lineEntries) {
        int index = this.headerIndex.get(CSVImportHeader.TEXT.name());
        if (lineEntries.length <= index) {
            log.warn("Line [" + lineNo + "]: Invalid line. TEXT component index out of range.");
            return;
        }
        String textValue = lineEntries[index];
        String upStreamId = null;
        String context = null;
        Integer priority = null;
        if (headerIndex.containsKey(CSVImportHeader.UPSTREAMID.name()) && lineEntries.length > headerIndex.get(CSVImportHeader.UPSTREAMID.name())) {
            upStreamId = lineEntries[headerIndex.get(CSVImportHeader.UPSTREAMID.name())];
            if (upStreamId != null && "".equalsIgnoreCase(upStreamId.trim())) {
                upStreamId = null;
            }
        }
        if (headerIndex.containsKey(CSVImportHeader.CONTEXT.name()) && lineEntries.length > headerIndex.get(CSVImportHeader.CONTEXT.name())) {
            context = lineEntries[headerIndex.get(CSVImportHeader.CONTEXT.name())];
            if (context != null && "".equalsIgnoreCase(context.trim())) {
                context = null;
            }
        }
        if (headerIndex.containsKey(CSVImportHeader.PRIORITY.name()) && lineEntries.length > headerIndex.get(CSVImportHeader.PRIORITY.name())) {
            String sPriority = lineEntries[headerIndex.get(CSVImportHeader.PRIORITY.name())];
            try {
                priority = Integer.parseInt(sPriority);
            } catch (Exception e) {
            }
        }

        dataElements.add(new ImportDataElementDto(textValue, upStreamId, context, priority));
    }

    public ImportDataPackageDto getImportDataPackageDto() {
        return dataElements.isEmpty() ? null : new ImportDataPackageDto(dataElements);
    }
}
