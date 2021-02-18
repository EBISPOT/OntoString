package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.config.ExternalServiceConfig;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.config.ExternalServiceConfigDto;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExternalServiceConfigDtoAssembler {

    public static ExternalServiceConfigDto assemble(ExternalServiceConfig externalServiceConfig) {
        Map<String, String> map = new HashMap<>();
        if (externalServiceConfig.getAliases() != null) {
            map = CurationUtil.parseAliases(externalServiceConfig.getAliases());
        }
        return new ExternalServiceConfigDto(externalServiceConfig.getName(), map);
    }

    public static ExternalServiceConfig disassemble(ExternalServiceConfigDto externalServiceConfig) {
        List<String> aliases = new ArrayList<>();
        if (externalServiceConfig.getAliases() != null) {
            for (String key : externalServiceConfig.getAliases().keySet()) {
                aliases.add(key + "::" + externalServiceConfig.getAliases().get(key));
            }
        }
        return new ExternalServiceConfig(null, externalServiceConfig.getServiceName(), aliases);
    }
}
