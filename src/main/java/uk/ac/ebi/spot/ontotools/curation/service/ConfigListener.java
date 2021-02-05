package uk.ac.ebi.spot.ontotools.curation.service;

import java.util.List;

public interface ConfigListener {

    void updateAliases(List<String> aliases);

    String getName();
}
