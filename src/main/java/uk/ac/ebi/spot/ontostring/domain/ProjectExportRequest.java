package uk.ac.ebi.spot.ontostring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "projectExportRequests")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectExportRequest {

    @Id
    private String id;

    @Indexed
    private String projectId;

    @Indexed
    private String requestId;

    @Indexed
    private String status;

    private String fileId;
}
