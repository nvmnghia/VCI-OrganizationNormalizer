package Config;

public class AppConfig {

    public final static String UETSegmenter_MODELS_PATH = "/home/vh/IdeaProjects/VCI-OrganizationNormalizer/UETsegmenter/models";

    //public final static String BASE_URL_VCGATE_API = "https://vcgate.vnu.edu.vn/api";
    public final static String BASE_URL_VCGATE_API = "http://localhost:8000/api";

    public static final String ORG_RULE_PATH = "org_rule/";
    public static final String VN_ORG_RULE_PATH = ORG_RULE_PATH + "vnOrgDict.txt";
    public static final String FOREIGN_ORG_RULE_PATH = ORG_RULE_PATH + "foreignOrgDict.txt";

}
