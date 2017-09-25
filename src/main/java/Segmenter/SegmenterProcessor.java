package Segmenter;

import Organization.Organization;
import Utilities.StringNormalizer;
import vn.edu.vnu.uet.nlp.segmenter.UETSegmenter;

import java.util.concurrent.ConcurrentHashMap;

/*
create list tokenized words
 */

public class SegmenterProcessor implements Runnable {
    private ConcurrentHashMap<String, Integer> tokenizedWords;
    private UETSegmenter segmenter;
    private Organization organization;

    public SegmenterProcessor(Organization organization, ConcurrentHashMap<String, Integer> tokenizedWords, UETSegmenter segmenter) {
        this.organization = organization;
        this.tokenizedWords = tokenizedWords;
        this.segmenter = segmenter;
    }

    @Override
    public void run() {
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
    }
}
