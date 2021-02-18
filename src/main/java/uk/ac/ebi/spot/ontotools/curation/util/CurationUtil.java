package uk.ac.ebi.spot.ontotools.curation.util;

import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.config.ProjectMappingConfig;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CurationUtil {

    public static List<ProjectRole> rolesFromStringList(List<String> list) {
        List<ProjectRole> projectRoles = new ArrayList<>();
        for (String pRole : list) {
            projectRoles.add(ProjectRole.valueOf(pRole.toUpperCase()));
        }
        return projectRoles;
    }

    public static List<String> listToLowerCase(List<String> list) {
        if (list == null) {
            return null;
        }
        return list.stream().map(String::toLowerCase).collect(Collectors.toList());
    }

    public static List<String> sToList(String s) {
        List<String> list = new ArrayList<>();
        if (s == null) {
            return list;
        }

        String[] parts = s.split(",");
        for (String part : parts) {
            part = part.trim();
            if (!"".equals(part)) {
                list.add(part);
            }
        }
        return list;
    }

    public static String ontoFromIRI(String iri) {
        int index = iri.lastIndexOf("/");
        String rest = iri.substring(index + 1);
        index = rest.indexOf("_");
        if (index == -1) {
            return iri;
        }
        return rest.substring(0, index);
    }

    public static Map<String, String> parseAliases(List<String> aliasList) {
        Map<String, String> map = new HashMap<>();
        if (aliasList == null) {
            return map;
        }

        for (String alias : aliasList) {
            String[] split = alias.split("::");
            if (split.length != 2) {
                continue;
            }

            map.put(split[0].trim().toLowerCase(), split[1].trim().toLowerCase());
        }
        return map;
    }

    public static List<ProjectMappingConfig> configListtoLowerCase(List<ProjectMappingConfig> projectMappingConfigs) {
        List<ProjectMappingConfig> result = new ArrayList<>();
        for (ProjectMappingConfig projectMappingConfig : projectMappingConfigs) {
            result.add(new ProjectMappingConfig(projectMappingConfig.getField().toLowerCase(), listToLowerCase(projectMappingConfig.getMappingList())));
        }
        return result;
    }

    public static List<String> configForField(Entity entity, List<ProjectMappingConfig> projectMappingConfigs) {
        if (entity.getBaseField() == null) {
            for (ProjectMappingConfig projectMappingConfig : projectMappingConfigs) {
                if (projectMappingConfig.getField().equalsIgnoreCase(ProjectMappingConfig.ALL)) {
                    return projectMappingConfig.getMappingList();
                }
            }

            if (!projectMappingConfigs.isEmpty()) {
                return projectMappingConfigs.get(0).getMappingList();
            }
        } else {
            for (ProjectMappingConfig projectMappingConfig : projectMappingConfigs) {
                if (projectMappingConfig.getField().equalsIgnoreCase(entity.getBaseField())) {
                    return projectMappingConfig.getMappingList();
                }
            }
            for (ProjectMappingConfig projectMappingConfig : projectMappingConfigs) {
                if (projectMappingConfig.getField().equalsIgnoreCase(ProjectMappingConfig.ALL)) {
                    return projectMappingConfig.getMappingList();
                }
            }
        }

        return new ArrayList<>();
    }
}
