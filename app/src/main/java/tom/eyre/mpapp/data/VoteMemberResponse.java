package tom.eyre.mpapp.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VoteMemberResponse {
    public VoteMemberResponse(){}

    @JsonProperty(value = "_about")
    private String about;
}
