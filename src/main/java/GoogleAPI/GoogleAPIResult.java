package GoogleAPI;

public class GoogleAPIResult {
    private String formatted_address;
    private String name;
    private String place_id;

    // Constructors
    public GoogleAPIResult() {
    }

    public GoogleAPIResult(String formattedAddress, String name, String place_id) {
        this.formatted_address = formattedAddress;
        this.name = name;
        this.place_id = place_id;
    }

    // Getters and Setters
    public String getFormattedAddress() {
        return formatted_address;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formatted_address = formattedAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return place_id;
    }

    public void setPlaceId(String place_id) {
        this.place_id = place_id;
    }
}
