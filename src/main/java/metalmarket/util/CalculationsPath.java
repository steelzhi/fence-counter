package metalmarket.util;

import metalmarket.enums.City;

public class CalculationsPath {
    private CalculationsPath() {
    }

    public static String getCalculationsDisk() {
        return "G:\\";
    }

    public static String getCalculationsTitle(City city) {
        String dateTimeString = DataHandler.getDateTime();
        String title = "Расчет по стоимости забора в г. " + (city == City.SAMARA ? "Самара" : "Тольятти") + " от "
                + dateTimeString;
        return title;
    }

    public static String getCalculationsTitleForCleaning() {
        return "Расчет по стоимости забора в г.";
    }
}
