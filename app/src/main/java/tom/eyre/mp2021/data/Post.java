package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Post {

    @JsonProperty(value = "@Id")
    private Integer postId;

    @JsonProperty(value = "Name")
    private String position;

    @JsonProperty(value = "HansardName")
    private String hansardName;

    @JsonProperty(value = "StartDate")
    private String startDate;

    @JsonProperty(value = "EndDate")
    private Object endDate;

    @JsonProperty(value = "Note")
    private String note;

    @JsonProperty(value = "EndNote")
    private String endNote;

    @JsonProperty(value = "IsJoint")
    private Boolean isJoint;

    @JsonProperty(value = "IsUnpaid")
    private Boolean isUnpaid;

    @JsonProperty(value = "Email")
    private String email;

    @JsonProperty(value = "LayingMinisterName")
    private String layingMinisterName;
}
