package Utl;

import info.debatty.java.stringsimilarity.Jaccard;

import java.util.*;

public class ComparisonAlgorithm {
    public enum ALGORITHM {
        JACCARD_QUERY, JACCARD_STANDARD
    }

    /**
     * Returns Jaccard's index of LCS for X[0..m-1], Y[0..n-1]
     *
     * @return
     */
    public static float JaccardIndexQuery(String query, String result) {
        String[] arrWordQuery = query.split(" ");

        ArrayList<String> arrWordResult = new ArrayList(Arrays.asList(result.split(" ")));
        Collections.sort(arrWordResult);

        int intersect = 0, index;
        for (String wordQuery : arrWordQuery) {
            if ((index = Collections.binarySearch(arrWordResult, wordQuery)) >= 0) {
                ++intersect;
                arrWordResult.remove(index);
            }
        }

        return ((float) intersect) / arrWordQuery.length;
    }

    public static double JaccardIndexStandard(String query, String result) {
        Jaccard jaccard = new Jaccard();
        return jaccard.distance(query, result);
    }
}