package BingAPI;

import Organization.Organization;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Random;

public class BingTest {
    public static void main(String[] args) throws IOException, URISyntaxException {
        File JSONFolder = new File("D:\\VCI\\organization_json");
        Gson gson = new Gson();
        Random random = new Random();

        for (File JSONFile : JSONFolder.listFiles()) {
            if (JSONFile.getName().endsWith(".json") && ! JSONFile.getName().endsWith("_response.json") && random.nextInt(100) == 0) {
                File outputFile = new File(JSONFile.getPath().replace(".json", "_response.json"));
                if (outputFile.isFile())
                    continue;

                String jsonContent = new String(Files.readAllBytes(JSONFile.toPath()));
                Organization organization = gson.fromJson(jsonContent, Organization.class);

                BingResult[] results = BingSearchCrawler.getBingResult(organization.getName(), outputFile);

                System.out.println(JSONFile.getName() + "    Query: " + organization.getName());
                for (BingResult result : results) {
                    System.out.println("Name: " + result.getName() + "    URL: " + result.getDisplayUrl());
                }
                System.out.println("");
            }
        }
    }
}
