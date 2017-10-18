package BingAPI;

public class BingResponse {
    private String query;
    private BingResult.BingResultList webPages;

    // Constructor
    public BingResponse() {
    }

    // Getters and Setters
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public BingResult.BingResultList getWebPages() {
        return webPages;
    }

    public void setWebPages(BingResult.BingResultList webPages) {
        this.webPages = webPages;
    }
}
