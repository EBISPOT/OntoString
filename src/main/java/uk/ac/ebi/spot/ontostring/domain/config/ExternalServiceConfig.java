package uk.ac.ebi.spot.ontostring.domain.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "externalServiceConfigs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalServiceConfig {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private List<String> aliases;

}
