package GoogleAPI;

public class GoogleAPIResponse {
    private String query;
    private GoogleAPIResult[] results;

    // Constructors
    public GoogleAPIResponse() {
    }

    // Getters and Setters
    public GoogleAPIResult[] getResults() {
        return results;
    }

    public void setResults(GoogleAPIResult[] results) {
        this.results = results;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
