package Segmenter;

import Organization.Organization;
import com.google.gson.Gson;
import jdk.internal.util.xml.impl.Pair;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class ManualClassify {

    static File vnDictFile = new File("D:\\VCI\\VCI-OrganizationNormalizer\\vn_org_dict.txt");
    static ArrayList<String> vnDict = new ArrayList<>();

    static File foreignDictFile = new File("D:\\VCI\\VCI-OrganizationNormalizer\\foreign_org_dict.txt");
    static ArrayList<String> foreignDict = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        File JSONTestFolder = new File("D:\\VCI\\test");

        Scanner scanner = new Scanner(System.in);
        Gson gson = new Gson();

        ArrayList<javafx.util.Pair<String, Boolean>> classificationResult = new ArrayList<>();

        for (File JSONFile : JSONTestFolder.listFiles()) {
            String fileContent = new String(Files.readAllBytes(JSONFile.toPath()));
            Boolean isVN = null;

            Organization organization = gson.fromJson(fileContent, Organization.class);
            System.out.println(organization.getName());

            System.out.print("Classify: ");
            while (true) {
                String temp = scanner.nextLine();

                if (temp.equals("t")) {
                    isVN = true;
                    break;
                }
                else if (temp.equals("f")) {
                    isVN = false;
                    break;
                }
                else if (temp.equals("n")) {
                    break;
                }
            }

            classificationResult.add(new javafx.util.Pair<>(JSONFile.getName(), isVN));
        }

//        File JSONTestFolder = new File("D:\\VCI\\test");
//        String temp;
//        Gson gson = new Gson();
//
//        BufferedReader reader = new BufferedReader(new FileReader(vnDictFile));
//        while ((temp = reader.readLine()) != null) {
//            vnDict.add(temp);
//        }
//
//        reader = new BufferedReader(new FileReader(foreignDictFile));
//        while ((temp = reader.readLine()) != null) {
//            foreignDict.add(temp);
//        }
//
//        for (File JSONFile : JSONTestFolder.listFiles()) {
//            JSONFile
//        }
    }

    private Boolean findLoop(String[] currentTokenizedWords) {
        for (String word : currentTokenizedWords) {
            for (String vnKeyword : vnDict) {
                if (word.equals(vnKeyword))
                    return true;
            }

            for (String foreignKeyword : foreignDict) {
                if (word.equals(foreignKeyword))
                    return false;
            }
        }

        return null;
    }
}
