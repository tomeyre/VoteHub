package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import lombok.Data;

@Data
public class VoteResponse {
    public VoteResponse(){}

    @JsonProperty(value = "memberParty")
    private String party;

    @JsonProperty(value = "memberPrinted")
    private VoteMpMemberResponse memberPrinted;

    @JsonProperty(value = "member")
    private ArrayList<VoteMemberResponse> member;

    @JsonProperty(value = "type")
    private String result;
}
