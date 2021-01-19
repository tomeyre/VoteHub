package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DivisionResponse {
    public DivisionResponse(){}

    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "_about")
    private String about;

    @JsonProperty(value = "uin")
    private String uin;

    @JsonProperty(value = "date")
    private DivisionDateResponse date;
}
