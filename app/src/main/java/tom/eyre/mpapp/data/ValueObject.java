package tom.eyre.mpapp.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import lombok.Data;

@Data
public class ValueObject implements Serializable {

    @JsonProperty(value = "_value")
    private String value;
}
