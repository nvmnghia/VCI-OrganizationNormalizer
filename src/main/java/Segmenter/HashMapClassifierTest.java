package Segmenter;

import Organization.Organization;
import Utl.StringNormalizer;
import com.google.gson.Gson;
import vn.edu.vnu.uet.nlp.segmenter.UETSegmenter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;

public class HashMapClassifierTest implements Runnable {
    private File JSONFile;
    private UETSegmenter segmenter;

    private HashSet<String> vnDictionary;
    private HashSet<String> foreignDictionary;

    public HashMapClassifierTest(File JSONFile, HashSet<String> vnDictionary, HashSet<String> foreignDictionary) {
        this.JSONFile = JSONFile;
        this.segmenter = segmenter;

        this.vnDictionary = vnDictionary;
        this.foreignDictionary = foreignDictionary;
    }

    @Override
    public void run() {
        segmenter = new UETSegmenter("D:\\VCI\\UETsegmenter-master\\models");
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
        Boolean isVN = findHashSet(currentTokenizedWords);
        System.out.println(organization.getName() + "    " + isVN);
    }

    private Boolean findHashSet(String[] currentTokenizedWords) {
        for (String word : currentTokenizedWords) {
            if (vnDictionary.contains(word))
                return true;

            if (foreignDictionary.contains(word))
                return false;
        }

        return null;
    }
}
