package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MpResponse {
    public MpResponse(){}

    @JsonProperty(value = "DisplayAs")
    private String displayAs;

    @JsonProperty(value = "DateOfBirth")
    private Object dateOfBirth;

    @JsonProperty(value = "HouseEndDate")
    private Object houseEndDate;

    @JsonProperty(value = "CurrentStatus")
    private MpCurrentStatusResponse currentStatus;

    @JsonProperty(value = "@Clerks_Id")
    private String clerksId;

    @JsonProperty(value = "HouseStartDate")
    private Object houseStartDate;

    @JsonProperty(value = "House")
    private String house;

    @JsonProperty(value = "Gender")
    private String gender;

    @JsonProperty(value = "@Member_Id")
    private Integer memberId;

    @JsonProperty(value = "LayingMinisterName")
    private String layingMinisterName;

    @JsonProperty(value = "@Dods_Id")
    private String dodsId;

    @JsonProperty(value = "Party")
    private MpPartyResponse Party;

    @JsonProperty(value = "FullTitle")
    private String fullTitle;

    @JsonProperty(value = "@Pims_Id")
    private String pimsId;

    @JsonProperty(value = "DateOfDeath")
    private Object dateOfDeath;

    @JsonProperty(value = "ListAs")
    private String listAs;

    @JsonProperty(value = "MemberFrom")
    private String memberFrom;

    @JsonProperty(value = "BasicDetails")
    private MpBasicDetailsResponse basicDetails;

    @JsonProperty(value = "Parties")
    private MpParties parties;

    @JsonProperty(value = "OppositionPosts")
    private OppositionPosts oppositionPosts;

    @JsonProperty(value = "ParliamentaryPosts")
    private ParliamentaryPosts parliamentaryPosts;

    @JsonProperty(value = "GovernmentPosts")
    private GovernmentPosts governmentPosts;

    @JsonProperty(value = "Interests")
    private Interests interests;
}
