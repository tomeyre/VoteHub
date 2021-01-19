package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MpCurrentStatusResponse {

    public MpCurrentStatusResponse(){}

    @JsonProperty(value = "StartDate")
    private Object startDate;

    @JsonProperty(value = "@IsActive")
    private Boolean active;

    @JsonProperty(value = "@Id")
    private String id;

    @JsonProperty(value = "Reason")
    private String reason;

    @JsonProperty(value = "Name")
    private String name;

}
