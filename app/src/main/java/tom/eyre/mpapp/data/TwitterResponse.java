package tom.eyre.mpapp.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TwitterResponse {

    @JsonProperty(value = "_value")
    private String url;
}
