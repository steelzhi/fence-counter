package metalmarket.util;

public class WareFilePath {
    private WareFilePath() {
    }

    public static String getUrlOfUnhandledFile() {
        return "https://metal-market.ru/upload/www_tovar.csv";
    }

    public static String getPathOfUnhandledFile() {
        return "C:\\dev\\Работа\\Расчет заборов\\Необработанные товары.csv";
    }

    public static String getPathOfHandledFile() {
        return "C:\\dev\\Работа\\Расчет заборов\\Обработанные товары.csv";
    }
}
