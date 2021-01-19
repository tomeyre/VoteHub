package tom.eyre.mp2021.data.google;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Query {

    private static final String KNOWLEDGE_GRAPH_SEARCH_URL = "https://kgsearch.googleapis.com/v1/";
    private static final Logger log = Logger.getLogger(String.valueOf(Query.class));
    private final int limit;
    private final String query;
    private final List<String> types;
    private final List<String> languages;
    private final String apiKey;

    public String getUrl() {
        return KNOWLEDGE_GRAPH_SEARCH_URL + "entities:search?limit=" + limit + "&query=" + query + formatTypes(types) + "&key=" + apiKey;
    }

    @Builder(builderMethodName = "ofPerson")
    public static Query person(String firstName, String lastName, String apiKey) {
        if (firstName == null || lastName == null || firstName.isEmpty() || lastName.isEmpty()) {
            throw new IllegalArgumentException("Person's name cannot be null or empty");
        }
        Query response = new Query(1, firstName + "+" + lastName + "+Parliament" , Arrays.asList("Person"), new ArrayList<String>(), apiKey);
        log.info(response.getUrl());
        return response;
    }

    private static String formatTypes(List<String> types) {
        if (types == null || types.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(String type : types){
            sb.append("&types=" + type);
        }

        return sb.toString();
    }
}
