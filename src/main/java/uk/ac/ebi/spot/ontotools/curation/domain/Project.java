package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "projects")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Project {

    @Id
    private String id;

    private String name;

    private String description;

    private Integer numberOfReviewsRequired;

    private List<String> contextIds;

    private Provenance created;

    @Transient
    private List<ProjectContext> contexts;
}
