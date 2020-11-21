package tom.eyre.mpapp.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MpBasicDetailsResponse {

    @JsonProperty(value = "GivenForename")
    private String forename;

    @JsonProperty(value = "GivenSurname")
    private String surname;
}
