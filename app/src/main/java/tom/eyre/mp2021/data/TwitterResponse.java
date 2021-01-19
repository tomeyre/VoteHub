package tom.eyre.mp2021.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TwitterResponse {

    @JsonProperty(value = "_value")
    private String url;
}
