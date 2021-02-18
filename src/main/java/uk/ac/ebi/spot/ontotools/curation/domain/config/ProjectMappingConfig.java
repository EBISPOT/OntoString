package uk.ac.ebi.spot.ontotools.curation.domain.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ProjectMappingConfig {

    public static final String ALL = "ALL";

    private String field;

    private List<String> mappingList;

}
