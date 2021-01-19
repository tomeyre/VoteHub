package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MpPartiesResponse {

    @JsonProperty(value = "@Id")
    private Integer id;

    @JsonProperty(value = "Name")
    private String name;

    @JsonProperty(value = "SubType")
    private String subType;

    @JsonProperty(value = "StartDate")
    private String startDate;

    @JsonProperty(value = "EndDate")
    private Object endDate;

    @JsonProperty(value = "Note")
    private String note;

    @JsonProperty(value = "SitsOppositeSideToParty")
    private Boolean sitsOppositeSideToParty;

}
