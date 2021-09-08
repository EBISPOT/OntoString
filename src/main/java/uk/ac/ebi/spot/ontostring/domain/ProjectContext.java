package uk.ac.ebi.spot.ontostring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "contexts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@CompoundIndexes({@CompoundIndex(name = "pName", def = "{'projectId': 1, 'name': 1}")})
public class ProjectContext {

    @Id
    private String id;

    private String name;

    @Indexed
    private String projectId;

    private String description;


    /**
     * List of datasources used to query Zooma to retrieve suggestions.
     * Zooma service uses this list to filter results pertaining only to these datasources.
     */
    private List<String> datasources;

    /**
     * List of ontologies used to query Zooma to retrieve suggestions.
     * Zooma service uses this list to filter results pertaining only to these ontologies.
     * <p>
     * NB: Orphanet prefix used by Zooma is `ordo`
     */
    private List<String> ontologies;

    /**
     * Ontology IDs used when creating ontology terms locally to query OLS for a mapping
     */
    private List<String> preferredMappingOntologies;

    private List<String> preferredMappingOntologiesLower;

    private ProjectContextGraphRestriction projectContextGraphRestriction;
}
