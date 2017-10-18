package Segmenter;

import Organization.Organization;
import Utl.StringNormalizer;
import com.google.gson.Gson;
import vn.edu.vnu.uet.nlp.segmenter.UETSegmenter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class LoopClassifierTest implements Runnable {
    private File JSONFile;
    private UETSegmenter segmenter;

    private ArrayList<String> vnDictionary;
    private ArrayList<String> foreignDictionary;

    public LoopClassifierTest(File JSONFile, UETSegmenter segmenter, ArrayList<String> vnDictionary, ArrayList<String> foreignDictionary) {
        this.JSONFile = JSONFile;
        this.segmenter = segmenter;

        this.vnDictionary = vnDictionary;
        this.foreignDictionary = foreignDictionary;
    }

    @Override
    public void run() {
//        UETSegmenter segmenter = new UETSegmenter("D:\\VCI\\UETsegmenter-master\\models");
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
        Boolean isVN = findLoop(currentTokenizedWords);
        System.out.println(organization.getName() + "    " + isVN);
    }

    private Boolean findLoop(String[] currentTokenizedWords) {
        for (String word : currentTokenizedWords) {
            for (String vnKeyword : vnDictionary) {
                if (word.equals(vnKeyword))
                    return true;
            }

            for (String foreignKeyword : foreignDictionary) {
                if (word.equals(foreignKeyword))
                    return false;
            }
        }

        return null;
    }

    private boolean findBinarySearch(String[] currentTokenizedWords) {
        return false;
    }

    private boolean findHashSet(String[] currentTokenizedWords) {
        return false;
    }
}
