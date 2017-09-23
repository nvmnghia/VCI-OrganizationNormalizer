package Organization;

public class Organization {
    private String id;
    private String name;
    private String nameEn;
    private String address;
    private String lft;
    private String rgt;
    private String parentID;
    private String fullName;

    // Constructors
    public Organization() {
    }

    public Organization(String id, String name, String nameEn, String address, String lft, String rgt, String parentID, String fullName) {
        this.id = id;
        this.name = name;
        this.nameEn = nameEn;
        this.address = address;
        this.lft = lft;
        this.rgt = rgt;
        this.parentID = parentID;
        this.fullName = fullName;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLft() {
        return lft;
    }

    public void setLft(String lft) {
        this.lft = lft;
    }

    public String getRgt() {
        return rgt;
    }

    public void setRgt(String rgt) {
        this.rgt = rgt;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
