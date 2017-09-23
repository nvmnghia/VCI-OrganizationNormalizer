package Segmenter;

import Organization.Organization;
import Utilities.StringNormalizer;
import com.google.gson.Gson;
import com.sun.javafx.util.Utils;
import vn.edu.vnu.uet.nlp.segmenter.UETSegmenter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

public class SegmenterProcessor implements Runnable {
    private File JSONFile;
    private ConcurrentHashMap<String, Integer> tokenizedWords;
    private UETSegmenter segmenter;

    public SegmenterProcessor(File JSONFile, ConcurrentHashMap<String, Integer> tokenizedWords, UETSegmenter segmenter) {
        this.JSONFile = JSONFile;
        this.tokenizedWords = tokenizedWords;
        this.segmenter = segmenter;
    }

    @Override
    public void run() {
        try {
            Gson gson = new Gson();
            String fileContent = new String(Files.readAllBytes(JSONFile.toPath()));

            Organization organization = gson.fromJson(fileContent, Organization.class);
            String segmentedName = segmenter.segment(organization.getName());
            System.out.println(segmentedName);

            String[] currentTokenizedWords = StringNormalizer.normalize(segmentedName).split(" ");
            for (String word : currentTokenizedWords) {
                Integer occurences = tokenizedWords.get(word);

                if (occurences == null) {    // New word
                    tokenizedWords.put(word, 1);
                } else {
                    tokenizedWords.put(word, occurences + 1);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
