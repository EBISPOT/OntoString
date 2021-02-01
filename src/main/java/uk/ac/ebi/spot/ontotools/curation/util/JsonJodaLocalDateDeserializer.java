package uk.ac.ebi.spot.ontotools.curation.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class JsonJodaLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        DateTimeFormatter isoDateTimeFormatter = DateTimeCommon.getIsoDateFormatter();
        String dateValueAsString = jsonParser.getValueAsString();
        LocalDate localDate = isoDateTimeFormatter.parseLocalDate(dateValueAsString);
        return localDate;
    }
}
