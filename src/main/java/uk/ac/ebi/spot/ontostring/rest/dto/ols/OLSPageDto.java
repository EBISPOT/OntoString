package uk.ac.ebi.spot.ontostring.rest.dto.ols;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OLSPageDto implements Serializable {

    private static final long serialVersionUID = 3439620764262802878L;

    @JsonProperty("size")
    private final Integer size;

    @JsonProperty("totalElements")
    private final Integer totalElements;

    @JsonProperty("totalPages")
    private final Integer totalPages;

    @JsonProperty("number")
    private final Integer number;

    @JsonCreator
    public OLSPageDto(@JsonProperty("size") Integer size,
                      @JsonProperty("totalElements") Integer totalElements,
                      @JsonProperty("totalPages") Integer totalPages,
                      @JsonProperty("number") Integer number) {
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.number = number;
    }

    public Integer getSize() {
        return size;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Integer getNumber() {
        return number;
    }
}
