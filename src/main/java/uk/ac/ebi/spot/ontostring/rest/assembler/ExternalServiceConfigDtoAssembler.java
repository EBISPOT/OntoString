package uk.ac.ebi.spot.ontostring.rest.assembler;

import uk.ac.ebi.spot.ontostring.domain.config.ExternalServiceConfig;
import uk.ac.ebi.spot.ontostring.util.CurationUtil;
import uk.ac.ebi.spot.ontostring.rest.dto.config.ExternalServiceConfigDto;

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
