package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

@Data
public class Category {

    @JsonProperty(value = "@Id")
    private Integer id;

    @JsonProperty(value = "@Name")
    private String name;

    @JsonProperty(value = "Interest")
    private List<Interest> Interest;
}
