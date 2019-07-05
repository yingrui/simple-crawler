package me.yingrui.simple.crawler.service.extractor;

public class ExtractorFactory {

    private static ExtractorFactory instance = new ExtractorFactory();

    public static ExtractorFactory getInstance() {
        return instance;
    }

    public Extractor create(String extractorClass, Object obj) {
        try {
            Class<?> clazz = getClass(extractorClass);
            Extractor extractor = (Extractor) clazz.newInstance();
            extractor.setSource(obj);
            return extractor;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Class<?> getClass(String extractorClass) throws ClassNotFoundException {
        try {
            return Class.forName(extractorClass);
        } catch (ClassNotFoundException e) {
            return Class.forName("me.yingrui.simple.crawler.service.extractor." + extractorClass);
        }
    }

}
