package uk.ac.ebi.spot.ontotools.curation.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.OntologyTermDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.ProvenanceDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.export.ExportEntityDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.export.ExportMappingDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.export.ExportMappingSuggestionDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class EntityDataCollector {

    private static final Logger log = LoggerFactory.getLogger(EntityDataCollector.class);

    private String projectId;

    private List<ExportEntityDto> exportEntityDtos;

    public EntityDataCollector(String projectId) {
        this.projectId = projectId;
        this.exportEntityDtos = new ArrayList<>();
    }

    public void add(Entity entity, List<Mapping> mappingList, List<MappingSuggestion> mappingSuggestionList) {
        List<ExportMappingSuggestionDto> mappingSuggestions = new ArrayList<>();
        List<ExportMappingDto> mappings = new ArrayList<>();

        if (mappingList != null) {
            for (Mapping mapping : mappingList) {
                mappings.add(new ExportMappingDto(OntologyTermDtoAssembler.assemble(mapping.getOntologyTerm()), mapping.isReviewed(),
                        mapping.getStatus(), ProvenanceDtoAssembler.assemble(mapping.getCreated())));
            }
        }
        if (mappingSuggestionList != null) {
            for (MappingSuggestion mappingSuggestion : mappingSuggestionList) {
                mappingSuggestions.add(new ExportMappingSuggestionDto(OntologyTermDtoAssembler.assemble(mappingSuggestion.getOntologyTerm()),
                        ProvenanceDtoAssembler.assemble(mappingSuggestion.getCreated())));
            }
        }

        exportEntityDtos.add(new ExportEntityDto(entity.getName(),
                entity.getBaseId(),
                entity.getBaseField(),
                mappingSuggestions,
                mappings
        ));
    }

    public ByteArrayOutputStream serialize() {
        ObjectMapper objectMapper = new ObjectMapper();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry(projectId + ".json");
            zos.putNextEntry(entry);
            zos.write(objectMapper.writeValueAsString(exportEntityDtos).getBytes());
            zos.closeEntry();
        } catch (IOException e) {
            log.error("[{}] Error encountered when creating file: {}", projectId, e.getMessage(), e);
            return null;
        }
        return baos;
    }
}
