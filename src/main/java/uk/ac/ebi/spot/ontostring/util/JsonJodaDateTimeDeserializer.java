package uk.ac.ebi.spot.ontostring.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class JsonJodaDateTimeDeserializer extends JsonDeserializer<DateTime> {

    @Override
    public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        DateTimeFormatter isoDateTimeFormatter = DateTimeCommon.getIsoDateTimeFormatter();
        String dateValueAsString = jp.getValueAsString();
        DateTime dateTime = isoDateTimeFormatter.parseDateTime(dateValueAsString);
        return dateTime;
    }
}
