package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Interest {

    @JsonProperty(value = "@Id")
    private Integer id;

    @JsonProperty(value = "@ParentId")
    private Integer parentId;

    @JsonProperty(value = "@LastAmendment")
    private String lastAmendment;

    @JsonProperty(value = "@LastAmendmentType")
    private String lastAmendmentType;

    @JsonProperty(value = "RegisteredInterest")
    private String registeredInterest;

    @JsonProperty(value = "RegisteredLate")
    private Boolean registeredLate;

    @JsonProperty(value = "Created")
    private String created;

    @JsonProperty(value = "Amended")
    private Object amended;

    @JsonProperty(value = "Deleted")
    private Object deleted;
}
