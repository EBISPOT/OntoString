package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import org.apache.commons.codec.digest.DigestUtils;
import uk.ac.ebi.spot.ontotools.curation.constants.TermStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTermContext;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.OntologyTermDto;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;

import java.util.Arrays;
import java.util.List;

public class OntologyTermDtoAssembler {

    public static OntologyTermDto assemble(OntologyTerm ontologyTerm, String projectId, String context) {
        return new OntologyTermDto(ontologyTerm.getCurie(),
                ontologyTerm.getIri(),
                ontologyTerm.getLabel(),
                CurationUtil.termStatusForContext(ontologyTerm, projectId, context),
                ontologyTerm.getDescription(),
                ontologyTerm.getCrossRefs());
    }

    public static OntologyTerm disassemble(OntologyTermDto ontologyTerm, String projectId, String context) {
        String status = ontologyTerm.getStatus() != null ? ontologyTerm.getStatus() : TermStatus.NEEDS_CREATION.name();
        List<OntologyTermContext> ontologyTermContexts = Arrays.asList(new OntologyTermContext[]{
                new OntologyTermContext(projectId, context, status)
        });
        return new OntologyTerm(null,
                ontologyTerm.getCurie(),
                ontologyTerm.getIri(),
                DigestUtils.sha256Hex(ontologyTerm.getIri()),
                ontologyTerm.getLabel(),
                ontologyTermContexts,
                ontologyTerm.getDescription(),
                ontologyTerm.getCrossRefs());
    }
}
