package uk.ac.ebi.spot.ontostring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MetadataEntry {

    private String key;

    private String value;

    private String action;
}
