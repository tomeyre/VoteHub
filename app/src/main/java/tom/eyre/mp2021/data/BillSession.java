package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import lombok.Data;

@Data
public class BillSession implements Serializable {

    @JsonProperty(value = "_about")
    private String about;

    @JsonProperty(value = "displayName")
    private String displayName;
}
