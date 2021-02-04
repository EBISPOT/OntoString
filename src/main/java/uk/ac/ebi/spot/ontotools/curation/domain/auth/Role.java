package uk.ac.ebi.spot.ontotools.curation.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Role {

    private String projectId;

    private ProjectRole role;
}
