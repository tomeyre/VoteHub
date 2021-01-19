package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

@Data
public class ParliamentaryPosts {

    @JsonProperty(value = "ParliamentaryPost")
    private List<Post> parliamentaryPosts;
}
