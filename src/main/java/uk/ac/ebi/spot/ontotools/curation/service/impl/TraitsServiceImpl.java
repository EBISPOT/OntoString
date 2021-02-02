package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.Trait;
import uk.ac.ebi.spot.ontotools.curation.repository.TraitRepository;
import uk.ac.ebi.spot.ontotools.curation.service.TraitsService;

import java.util.List;

@Service
public class TraitsServiceImpl implements TraitsService {

    private static final Logger log = LoggerFactory.getLogger(TraitsService.class);

    @Autowired
    private TraitRepository traitRepository;

    @Override
    public List<Trait> getTraits() {
        log.info("Request to retrieve all traits.");
        return traitRepository.findAll();
    }
}
