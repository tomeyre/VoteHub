package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import lombok.Data;

@Data
public class SocialMediaResponse implements Serializable {

    @JsonProperty(value = "homePage")
    private String homePage;

    @JsonProperty(value = "twitter")
    private TwitterResponse twitter;
}
