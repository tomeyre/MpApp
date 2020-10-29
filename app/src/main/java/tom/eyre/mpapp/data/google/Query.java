package tom.eyre.mpapp.data.google;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Query {
    private static final String KNOWLEDGE_GRAPH_SEARCH_URL = "https://kgsearch.googleapis.com/v1/";

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
        System.out.println(firstName + " " + lastName);
        if (firstName == null || lastName == null || firstName.isEmpty() || lastName.isEmpty()) {
            throw new IllegalArgumentException("Person's name cannot be null or empty");
        }
        return new Query(1, firstName + "+" + lastName, Arrays.asList("Person"), new ArrayList<String>(), apiKey);
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
