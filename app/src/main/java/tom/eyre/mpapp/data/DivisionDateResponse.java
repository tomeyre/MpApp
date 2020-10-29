package tom.eyre.mpapp.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DivisionDateResponse {

    public DivisionDateResponse(){}

    @JsonProperty(value = "_value")
    private String value;

    @JsonProperty(value = "_dataType")
    private String dataType;

}
