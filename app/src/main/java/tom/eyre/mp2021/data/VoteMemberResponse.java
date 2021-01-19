package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class VoteMemberResponse {
    public VoteMemberResponse(){}

    @JsonProperty(value = "_about")
    private String about;
}
