package Utl;

public class ArrayUtl {
    public static int idxMax(float[] array) {
        if (array.length == 0) {
            return -1;
        }

        int idxMax = 0;
        float tmpMax = array[0];

        for (int i = 0; i < array.length; ++i) {
            if (tmpMax < array[i]) {
                idxMax = i;
                tmpMax = array[i];
            }
        }

        return idxMax;
    }

    public static int idxMax(double[] array) {
        if (array.length == 0) {
            return -1;
        }

        int idxMax = 0;
        double tmpMax = array[0];

        for (int i = 0; i < array.length; ++i) {
            if (tmpMax < array[i]) {
                idxMax = i;
                tmpMax = array[i];
            }
        }

        return idxMax;
    }
}
