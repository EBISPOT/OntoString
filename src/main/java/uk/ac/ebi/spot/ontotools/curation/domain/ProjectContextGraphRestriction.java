package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ProjectContextGraphRestriction {

    private List<String> classes;

    private List<String> iris;

    private List<String> relations;

    private Boolean direct;

    private Boolean includeSelf;

}
