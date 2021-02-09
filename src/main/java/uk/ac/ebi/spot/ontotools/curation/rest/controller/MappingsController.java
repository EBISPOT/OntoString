package uk.ac.ebi.spot.ontotools.curation.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.service.*;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_MAPPINGS)
public class MappingsController {

    private static final Logger log = LoggerFactory.getLogger(ProjectsController.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private MappingSuggestionsService mappingSuggestionsService;

    @Autowired
    private UserService userService;


    /**
     * GET /v1/mappings?entityId=<entityId>
     */

    /**
     * POST /v1/mappings
     */

    /**
     * PUT /v1/mappings/{mappingId}
     * - sets mapping active
     */

    /**
     * DELETE /v1/mappings/{mappingId}
     */

    /**
     * TODO:
     * - Add comments
     * - Add review
     */
}
