package uk.ac.ebi.spot.ontotools.curation.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class JsonJodaLocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        DateTimeFormatter isoDateTimeFormatter = DateTimeCommon.getIsoDateFormatter();
        jsonGenerator.writeString(isoDateTimeFormatter.print(localDate));
    }
}
