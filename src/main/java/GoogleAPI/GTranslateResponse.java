package GoogleAPI;

public class GTranslateResponse {
    private Data data;

    public GTranslateResponse(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    // Inner classes
    public static class Data {
        public Translation[] translations;

        public Data() {
        }

        public Translation[] getTranslations() {
            return translations;
        }

    }

    public static class Translation {
        public String translatedText;

        public Translation() {
        }

        public String getTranlatedText() {
            return translatedText;
        }
    }
}
