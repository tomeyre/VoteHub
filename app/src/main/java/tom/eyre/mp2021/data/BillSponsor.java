package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class BillSponsor implements Serializable {

    public BillSponsor(){}

    @JsonProperty(value = "_about")
    private String about;

    @JsonProperty(value = "sponsorPrinted")
    private List<String> sponsorPrinted;

}
