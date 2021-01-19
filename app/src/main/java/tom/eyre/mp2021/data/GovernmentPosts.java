package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

@Data
public class GovernmentPosts {

    @JsonProperty(value = "GovernmentPost")
    private List<Post> governmentPosts;
}
