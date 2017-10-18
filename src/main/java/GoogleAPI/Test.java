package GoogleAPI;

import Organization.Organization;
import Utl.ComparisonAlgorithm;
import com.google.gson.Gson;
import org.apache.http.client.utils.URIBuilder;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.*;

import static GoogleAPI.GoogleMapCrawler.bestResult;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        GoogleSearchScraper.scrap(new File("D:\\VCI\\organization_json"));
    }

    public static String editStr(String str) {
        str = str + "bar";

        return str;
    }

    public static void process(File input, File output) throws IOException {
        StringBuilder builder = new StringBuilder("");
        BufferedReader reader = new BufferedReader(new FileReader(input));

        List<String> tmpBlock = new ArrayList<>();
        String tmpLine = "";
        GoogleAPIResult tmpResult1, tmpResult2;

        while ((tmpLine = reader.readLine()) != null) {
            if (tmpLine.length() == 0 && tmpBlock.size() > 0) {
                String query = discardShit(tmpBlock.get(tmpBlock.size() - 1));
                GoogleAPIResult[] results = getListResults(tmpBlock);

                builder.append(query).append('\n');

                if ((query.contains("Phòng") || query.contains("Khoa")) && !(query.contains("Trường") || query.contains("Đại học")))
                    continue;

                if ((query.contains("Dept") || query.contains("Department")) && !(query.contains("Univ")))
                    continue;

                tmpResult1 = bestResult(query, results, ComparisonAlgorithm.ALGORITHM.JACCARD_QUERY);
                builder.append("Jaccard Query:    ").append(tmpResult1 == null ? null : tmpResult1.getName()).append('\n');

                tmpResult2 = bestResult(query, results, ComparisonAlgorithm.ALGORITHM.JACCARD_STANDARD);
                builder.append("Jaccard Standard: ").append(tmpResult2 == null ? null : tmpResult2.getName()).append('\n');

                if (tmpResult1 != null && tmpResult2 != null && ! tmpResult1.getName().equals(tmpResult2.getName())) {
                    builder.append("DIFFERENT");
                }

                builder.append('\n');
                tmpBlock = new ArrayList<>();
            } else {
                tmpBlock.add(tmpLine);
            }
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(output));
        writer.write(builder.toString());
        writer.close();
    }


    public static String discardShit(String line) {
        if (line.startsWith("-1 ")) {
            return line.substring(3, line.length());
        } else {
            return line.substring(2, line.length());
        }
    }

    public static GoogleAPIResult[] getListResults(String query) {
        try {
            URIBuilder builder = new URIBuilder(AppConfig.GOOGLE_MAP_QUERY);
            builder.addParameter("query", query);
            builder.addParameter("key", AppConfig.GOOGLE_KEY);

            URLConnection connection = builder.build().toURL().openConnection();
            System.out.println("URL: " + builder.build().toURL().toString());

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine, content;
            StringBuilder stringBuilder = new StringBuilder("");

            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine).append('\n');
            }
            in.close();
            content = stringBuilder.toString();

            Gson gson = new Gson();
            GoogleAPIResponse response = gson.fromJson(content, GoogleAPIResponse.class);

            return response.getResults();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return new GoogleAPIResult[0];
    }

    public static GoogleAPIResult[] getListResults(List<String> block) {
        GoogleAPIResult[] results = new GoogleAPIResult[block.size() - 1];

        if (block.size() == 1)
            return new GoogleAPIResult[0];

        for (int i = block.size() - 2; i >= 0; i--) {
            results[i] = new GoogleAPIResult();
            int brk = block.get(i).indexOf(" - ");
            results[i].setName(block.get(i).substring(0, brk == -1 ? 0 : brk));
        }

        return results;
    }

    public static void processFiles(File folder) throws IOException, URISyntaxException {
        Random random = new Random();

        for (File JSONFile : folder.listFiles()) {
            if (JSONFile.getName().endsWith(".json") && random.nextInt(3) == 0) {
                try {
                    StringBuilder fileOutputContent = new StringBuilder("");
                    Gson gson = new Gson();
                    String JSONFileContent = new String(Files.readAllBytes(JSONFile.toPath()));
                    Organization organization = gson.fromJson(JSONFileContent, Organization.class);
                    String query = organization.getName();

                    fileOutputContent.append(query).append('\n');

                    if ((query.contains("Phòng") || query.contains("Khoa")) && !(query.contains("Trường") || query.contains("Đại học")))
                        continue;

                    if ((query.contains("Dept") || query.contains("Department")) && !(query.contains("Univ")))
                        continue;

                    URIBuilder builder = new URIBuilder(AppConfig.GOOGLE_MAP_QUERY);
                    builder.addParameter("query", organization.getName());
                    builder.addParameter("key", AppConfig.GOOGLE_KEY);

                    URLConnection connection = builder.build().toURL().openConnection();
                    System.out.println("URL: " + builder.build().toURL().toString());

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine, content;
                    StringBuilder stringBuilder = new StringBuilder("");

                    while ((inputLine = in.readLine()) != null) {
                        stringBuilder.append(inputLine).append('\n');
                    }
                    in.close();
                    content = stringBuilder.toString();

                    GoogleAPIResponse response = gson.fromJson(content, GoogleAPIResponse.class);
                    GoogleAPIResult[] results = response.getResults();

                    GoogleAPIResult tmpResult1 = bestResult(query, results, ComparisonAlgorithm.ALGORITHM.JACCARD_QUERY);
                    fileOutputContent.append("Jaccard Query:    ").append(tmpResult1 == null ? null : tmpResult1.getName()).append('\n');

                    GoogleAPIResult tmpResult2 = bestResult(query, results, ComparisonAlgorithm.ALGORITHM.JACCARD_STANDARD);
                    fileOutputContent.append("Jaccard Standard: ").append(tmpResult2 == null ? null : tmpResult2.getName()).append('\n');

                    if (tmpResult1 != null && tmpResult2 != null && ! tmpResult1.getName().equals(tmpResult2.getName())) {
                        fileOutputContent.append("DIFFERENT");
                    }
                    fileOutputContent.append('\n');

                    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(JSONFile.getPath().replace(".json", "_response.json"))));
                    writer.write(fileOutputContent.toString());
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Temp {
        private String str;

        public Temp(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }
    }
}
