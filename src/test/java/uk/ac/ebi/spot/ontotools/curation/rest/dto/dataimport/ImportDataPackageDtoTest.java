package uk.ac.ebi.spot.ontotools.curation.rest.dto.dataimport;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ImportDataPackageDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ImportDataPackageDto.class)
                .verify();
    }

}