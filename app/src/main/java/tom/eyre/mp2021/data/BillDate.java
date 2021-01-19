package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import lombok.Data;

@Data
public class BillDate implements Serializable {

    @JsonProperty(value = "_value")
    private String value;

    @JsonProperty(value = "_datatype")
    private String type;
}
