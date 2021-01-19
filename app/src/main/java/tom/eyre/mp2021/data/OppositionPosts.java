package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

@Data
public class OppositionPosts {

    @JsonProperty(value = "OppositionPost")
    private List<Post> oppositionPosts;
}
