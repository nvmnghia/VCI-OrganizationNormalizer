package Segmenter;

import Organization.Organization;
import Utl.StringNormalizer;
import com.google.gson.Gson;
import vn.edu.vnu.uet.nlp.segmenter.UETSegmenter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

public class BinarySearch implements Runnable {
    private File JSONFile;

    // Sorted in ascending order
    private ArrayList<String> vnDictionary;
    private ArrayList<String> foreignDictionary;

    public BinarySearch(File JSONFile, ArrayList<String> vnDictionary, ArrayList<String> foreignDictionary) {
        this.JSONFile = JSONFile;

        this.vnDictionary = vnDictionary;
        this.foreignDictionary = foreignDictionary;
    }


    @Override
    public void run() {
        UETSegmenter segmenter = new UETSegmenter("D:\\VCI\\UETsegmenter-master\\models");
        Gson gson = new Gson();

        String fileContent = null;
        try {
            fileContent = new String(Files.readAllBytes(JSONFile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Organization organization = gson.fromJson(fileContent, Organization.class);
        String segmentedName = segmenter.segment(organization.getName());

        String[] currentTokenizedWords = StringNormalizer.normalize(segmentedName).split(" ");
        Boolean isVN = findBinarySearch(currentTokenizedWords);
        System.out.println(organization.getName() + "    " + isVN);
    }

    private Boolean findBinarySearch(String[] currentTokenizedWords) {
        int high, mid, low, comp;
        String midString;

        for (String word : currentTokenizedWords) {
            high = vnDictionary.size() - 1;
            low = 0;

            while (low <= high) {
                mid = (low + high) >>> 1;
                midString = vnDictionary.get(mid);
                comp = midString.compareTo(word);

                if (comp < 0)
                    low = mid + 1;
                else if (comp > 0)
                    high = mid - 1;
                else
                    return true;    // Found keyword!
            }

            high = foreignDictionary.size() - 1;
            low = 0;

            while (low <= high) {
                mid = (low + high) >>> 1;
                midString = foreignDictionary.get(mid);
                comp = midString.compareTo(word);

                if (comp < 0)
                    low = mid + 1;
                else if (comp > 0)
                    high = mid - 1;
                else
                    return false;    // Found keyword!
            }
        }

        return null;
    }
}
