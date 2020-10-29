package tom.eyre.mpapp.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MpPartyResponse {
    public MpPartyResponse(){}

    @JsonProperty(value = "#text")
    private String text;

    @JsonProperty(value = "@Id")
    private String id;
}