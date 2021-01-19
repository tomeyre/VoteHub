package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class BillResponse implements Serializable {

    public BillResponse(){}

    @JsonProperty(value = "_about")
    private String about;

    @JsonProperty(value = "billType")
    private String type;

    @JsonProperty(value = "date")
    private BillDate date;

    @JsonProperty(value = "homePage")
    private String homePage;

    @JsonProperty(value = "identifier")
    private ValueObject identifier;

    @JsonProperty(value = "label")
    private ValueObject label;

    @JsonProperty(value = "session")
    private List<BillSession> session;

    @JsonProperty(value = "sponsors")
    private List<BillSponsor> sponsors;

    @JsonProperty(value = "title")
    private String title;
}
