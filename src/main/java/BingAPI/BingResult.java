package BingAPI;

public class BingResult {
    private String name;
    private String displayUrl;

    // Constructors
    public BingResult() {
    }

    public BingResult(String name, String displayUrl) {
        this.name = name;
        this.displayUrl = displayUrl;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public static class BingResultList {
        public BingResult[] value;
    }
}
