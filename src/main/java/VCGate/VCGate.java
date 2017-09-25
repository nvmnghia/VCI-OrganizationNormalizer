package VCGate;

import Organization.Organization;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;

import static Config.AppConfig.*;

public class VCGate {
    private HashSet<String> vnOrgDictionary = this.getOrgDictionary(true);
    private HashSet<String> foreignOrgDictionary = this.getOrgDictionary(false);

    /*
    Get organizations from api by type
    1: vn org
    0: foreign org
    null: none
     */
    public Organization[] getOrganizations(Integer type) throws Exception {
        String urlString = BASE_URL_VCGATE_API + "/elasticsearch/getOrganizesName/";
        if(type == null) {
            urlString += "null";
        } else if(type == 0 || type == 1) { //vn or foreign
            urlString += type;
        } //else: all

        return (new Gson()).fromJson(this.readURL(urlString), Organization[].class);
    }

    /*
    update org: vn or foreign
     */
    public void updateOrganizationOrigin(Integer id, boolean isVn) throws Exception {
        String urlString = BASE_URL_VCGATE_API + "/organizes/" + id + "/isVn/" + isVn;

        this.readURL(urlString);
    }

    /*
    check if org is vn(1) or foreign(0) org from dictionaries
    -1: undecided
     */
    public Integer isVnOrg(String[] orgName) {
        for(String word: orgName) {
            if(this.vnOrgDictionary.contains(word))
                return 1;
            if(this.foreignOrgDictionary.contains(word))
                return 0;
        }
        return -1;
    }

    private HashSet<String> getOrgDictionary(boolean isVn) {
        String url;
        if(isVn) {
            url = VN_ORG_RULE_PATH;
        } else {
            url = FOREIGN_ORG_RULE_PATH;
        }
        HashSet<String> words = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(url))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    private String readURL(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

}
