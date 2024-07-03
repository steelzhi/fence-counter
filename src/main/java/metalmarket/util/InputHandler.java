package metalmarket.util;

import lombok.NoArgsConstructor;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class InputHandler {
    private static List<String> goodsNames = new ArrayList<>();
    private long MILLISECONDS_IN_ONE_DAY = 86_400_000;

    public void startHandling(long firstDelay) {
        Thread mainThread = new Thread(() -> {
            try {
                handleInputFileAndSave();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (firstDelay < MILLISECONDS_IN_ONE_DAY) {
                try {
                    Thread.sleep(firstDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Thread.sleep(MILLISECONDS_IN_ONE_DAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        mainThread.start();
    }

    public static void updateFileWithGoods() throws IOException {
        String fileUrl = "https://metal-market.ru/upload/www_tovar.csv";
        URL url = new URL(fileUrl);
        URLConnection urlConnection = url.openConnection();
        String fileName = "C:\\dev\\Работа\\Расчет заборов\\Необработанные товары.csv";
        String fileWithLastUpdateDateTime = "Последняя дата скачивания файла с товаром.txt";
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream(), Charset.forName("windows-1251")));
             BufferedWriter bufferedWriter = new BufferedWriter(
                     new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            String string;
            addGoodsNames();
            while ((string = bufferedReader.readLine()) != null) {
                if (doesStringContainProduct(string, goodsNames)) {
                    bufferedWriter.write(string);
                    bufferedWriter.newLine();
                }
            }
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileWithLastUpdateDateTime))) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            bufferedWriter.write(dateTimeFormatter.format(LocalDateTime.now()));
        }
    }

    private void handleInputFileAndSave() throws IOException {
        String fileName = "C:\\dev\\Работа\\Расчет заборов\\Необработанные товары.csv";
        List<String> unhandledGoods = getGoodsFromInitialFile(fileName);
        List<List<String>> handledGoods = new ArrayList<>();
        addCategoriesAndTitle(handledGoods);
        for (String unhandledGood : unhandledGoods) {
            if (unhandledGood.startsWith("\"3XTГЛВ")) {
                System.out.println();
            }
            String handled = getHandledGoods(unhandledGood);
            addHandledGoods(handledGoods, handled);
        }
        writeDataToFile(handledGoods);

        System.out.println();
    }

    public static void writeDataToFile(List<List<String>> goods) throws IOException {
        String fileName = "C:\\dev\\Работа\\Расчет заборов\\Обработанные товары.csv";
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            for (List<String> list : goods) {
                for (String good : list) {
                    bufferedWriter.write(good);
                    bufferedWriter.newLine();
                }
                bufferedWriter.newLine();
            }
        }
    }

    private void addHandledGoods(List<List<String>> handledGoods, String goods) {
        String[] params = goods.split("\";\"");

        if (params[1].startsWith("Профнастил")
                || params[1].startsWith("Штакетник")
                || params[1].startsWith("Сетка рабица")
                || params[1].startsWith("Панель")
        ) {
            handledGoods.get(1).add("\"П-" + goods.substring(1));
        } else if (params[1].startsWith("Столб")) {
            handledGoods.get(2).add("\"Ст-" + goods.substring(1));
        } else if (params[1].startsWith("Труба профильная")
                || params[1].startsWith("Проволока вязальная")
        ) {
            handledGoods.get(3).add("\"Н-" + goods.substring(1));
        } else if (params[1].startsWith("Калитка")
                || params[1].startsWith("Засов")
                || params[1].startsWith("Соединитель проф.трубы")
                || params[1].startsWith("Заглушки")
        ) {
            handledGoods.get(4).add("\"Со-" + goods.substring(1));

        }
    }

    private String getHandledGoods(String goods) {
        String[] params = goods.substring(1, goods.length() - 1).split("\";\"");
        String[] outputParamsArray = new String[16];
        outputParamsArray[0] = params[0];

        StringBuilder sb = new StringBuilder();
        if (params[1].startsWith("Профнастил") && !params[1].startsWith("Профнастил С21")) {
            outputParamsArray[1] = "Профнастил";
            String[] subParams = params[1].split(";");
            outputParamsArray[2] = subParams[1].trim().split(" ")[0];
            outputParamsArray[5] = params[4].split("х")[1];
            outputParamsArray[6] = params[5].split("х")[1];
            outputParamsArray[7] = params[4].split("х")[0];
            outputParamsArray[8] = params[5].split("х")[0];
            for (int i = 0; i < subParams[0].length(); i++) {
                if (subParams[0].charAt(i) == '0') {
                    outputParamsArray[9] = subParams[0].substring(i, i + 4);
                    break;
                }
            }
            String trimmedNameString = subParams[2].trim();
            String[] subsubParams = trimmedNameString.split(" ");
            if (subsubParams.length == 1) {
                outputParamsArray[10] = "Оцинкованный (неокрашенный)";
            } else {
                int firstSpacePos = trimmedNameString.indexOf(' ');
                outputParamsArray[10] = trimmedNameString.substring(firstSpacePos + 1);
            }
        } else if (params[1].startsWith("Штакетник")) {
            outputParamsArray[1] = "Штакетник";
            String[] subParams = params[1].split(";");
            int indexOfType = subParams[2].trim().indexOf(" ") + 1;
            outputParamsArray[2] = subParams[2].trim().substring(indexOfType);
            outputParamsArray[5] = params[4].split("х")[1];
            outputParamsArray[6] = params[5].split("х")[1];
            outputParamsArray[7] = params[4].split("х")[0];
            outputParamsArray[8] = params[5].split("х")[0];
            outputParamsArray[9] = subParams[1].trim();
            int indexOfColor = params[1].indexOf("Штакетник оц. ") + "Штакетник оц. ".length();
            outputParamsArray[10] = subParams[0].substring(indexOfColor);
        } else if (params[1].startsWith("Сетка стальная плет.")
                || params[1].startsWith("Сетка оц. плет.")
                || params[1].startsWith("Сетка плетен. полимер.")) {
            outputParamsArray[1] = "Сетка рабица";
            String[] subParams = params[1].split(";")[0].split(" ");
            String dimension = subParams[subParams.length - 2];
            outputParamsArray[3] = dimension.substring(0, dimension.length() - 1);
            outputParamsArray[4] = subParams[subParams.length - 1];
            outputParamsArray[5] = params[4].split("х")[0];
            outputParamsArray[6] = params[5].split("х")[0];
            outputParamsArray[7] = params[4].split("х")[1];
            outputParamsArray[8] = params[5].split("х")[1];
            if (params[1].startsWith("Сетка стальная плет.")) {
                outputParamsArray[10] = "Черная (без покрытия)";
            } else if (params[1].startsWith("Сетка оц. плет.")) {
                outputParamsArray[10] = "Оцинкованная";
            } else if (params[1].startsWith("Сетка плетен. полимер.")) {
                String[] subParams2 = params[1].split(";");
                outputParamsArray[10] = "С полимерным покрытием (" + subParams2[1].trim() + ")";
            }
        } else if (params[1].startsWith("Панели")) {
            outputParamsArray[1] = "Панель заборная";
            String[] subParams = params[1].split(";");
            int indexOfDimension = params[1].indexOf("Панели заборные оц. ") + "Панели заборные оц. ".length();
            String dimension = subParams[0].substring(indexOfDimension).split(" ")[0];
            outputParamsArray[3] = dimension.substring(0, dimension.length() - 1);
            outputParamsArray[5] = params[4].split("х")[0];
            outputParamsArray[6] = params[5].split("х")[0];
            outputParamsArray[7] = params[4].split("х")[1];
            outputParamsArray[8] = params[5].split("х")[1];
            int indexOfThickness = subParams[0].indexOf(outputParamsArray[3]) + outputParamsArray[3].length() + 2;
            outputParamsArray[9] = subParams[0].substring(indexOfThickness).trim();
            outputParamsArray[10] = subParams[1].trim();
        } else if (params[1].startsWith("Столб")) {
            outputParamsArray[1] = "Столб";
            String[] subParams = params[1].split(";");
            String[] subSubParams = subParams[0].split(" ");

            if (params[1].startsWith("Столб н/к круглый")) {
                outputParamsArray[2] = subSubParams[2];
                outputParamsArray[4] = subSubParams[4];
                outputParamsArray[5] = subSubParams[3].substring(0, subSubParams[3].length() - 1);
                outputParamsArray[9] = subParams[1].trim();
            } else if (params[1].startsWith("Столб н/к профильный")) {
                outputParamsArray[2] = "профильный";
                outputParamsArray[3] = subSubParams[4];
                outputParamsArray[5] = subSubParams[3].substring(0, subSubParams[3].length() - 1);
                outputParamsArray[9] = subParams[1];
            } else {
                outputParamsArray[2] = "профильный";
                outputParamsArray[3] = subSubParams[5];
                outputParamsArray[5] = subSubParams[4];
                outputParamsArray[9] = subParams[1];
                outputParamsArray[10] = subParams[2].trim();
            }
            outputParamsArray[6] = outputParamsArray[5];
        } else if (params[1].startsWith("Трубы проф. 40х20")) {
            outputParamsArray[1] = "Труба профильная";
            String[] subParams = params[1].split(";");
            String[] subSubParams = subParams[0].split(" ");

            outputParamsArray[3] = subSubParams[2].substring(0, subSubParams[2].length() - 1);
            outputParamsArray[5] = params[4];
            outputParamsArray[6] = params[5];
            outputParamsArray[9] = subSubParams[3];
        } else if (params[1].startsWith("Проволока с терм.")
                || params[1].startsWith("Проволока оцинк.с терм.")) {
            outputParamsArray[1] = "Проволока вязальная";
            String[] subParams = params[1].split(" ");
            outputParamsArray[4] = subParams[subParams.length - 1];
            if (params[1].startsWith("Проволока оцинк.")) {
                outputParamsArray[10] = "Оцинкованная";
            } else {
                outputParamsArray[10] = "Черная";
            }

            outputParamsArray[15] = "Вес 1 ед. в Самаре - " + params[4] + " кг, в Тольятти - " + params[5] + " кг";
        } else if (params[1].startsWith("Калитка")) {
            outputParamsArray[1] = "Калитка";
            String[] subParams = params[1].split(";");
            outputParamsArray[3] = subParams[1];
            outputParamsArray[5] = params[4].split("х")[0];
            outputParamsArray[6] = params[5].split("х")[0];
            outputParamsArray[7] = params[4].split("х")[1];
            outputParamsArray[8] = params[5].split("х")[1];
        } else if (params[1].startsWith("Засов")) {
            outputParamsArray[1] = "Засов";
            String[] subParams = params[1].split(" ");
            if (subParams.length > 4) {
                outputParamsArray[2] = subParams[3];
            } else {
                outputParamsArray[2] = subParams[1] + " " + subParams[2];
            }
            outputParamsArray[5] = params[4];
            outputParamsArray[6] = params[5];
        } else if (params[1].startsWith("Соединитель проф.трубы")) {
            outputParamsArray[1] = "Х-кронштейн для крепления прожилины к столбу";
        } else if (params[1].startsWith("Заглушки пластиковые")) {
            outputParamsArray[1] = "Заглушки пластиковые";
            String[] subParams = params[1].split("; ");
            outputParamsArray[2] = subParams[1] + ", " + subParams[2];
            int indexOfParams = params[1].indexOf(outputParamsArray[1]) + outputParamsArray[1].length() + 1;
            outputParamsArray[3] = subParams[0].substring(indexOfParams);
        }

        outputParamsArray[11] = params[8];
        outputParamsArray[12] = params[12];
        outputParamsArray[13] = params[14];
        outputParamsArray[14] = params[15];

        for (String outputParam : outputParamsArray) {
            sb.append("\"");
            if (outputParam != null) {
                sb.append(outputParam);
            }
            sb.append("\";");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private void addCategoriesAndTitle(List<List<String>> listOfCategories) {
        List<String> title = new ArrayList<>();
        title.add("Id(0) | Название(1) | Тип(2) | Сечение/Ячейка(3) | Диаметр(4) | Высота листа, рулона/длина хлыста C(5) | " +
                "Высота листа, рулона/длина хлыста Т(6)| Ширина листа/длина рулона С(7) | Ширина листа/длина рулона Т(8) | " +
                "Толщина(9) | Цвет/Покрытие(10) | Цена С(11) | Цена Т(12) | Наличие С(13) | Наличие Т(14) | Примечание(15)");
        List<String> cover = new ArrayList<>();
        cover.add("Покрытие:");
        List<String> pillars = new ArrayList<>();
        pillars.add("Столбы:");
        List<String> rails = new ArrayList<>();
        rails.add("Направляющие:");
        List<String> related = new ArrayList<>();
        related.add("Сопутствующие:");
        listOfCategories.add(title);
        listOfCategories.add(cover);
        listOfCategories.add(pillars);
        listOfCategories.add(rails);
        listOfCategories.add(related);
    }

    public static void addGoodsNames() {
        if (goodsNames.isEmpty()) {
            goodsNames.add("Профнастил");
            goodsNames.add("Штакетник");
            goodsNames.add("Сетка стальная плет.");
            goodsNames.add("Сетка оц. плет.");
            goodsNames.add("Сетка плетен. полимер.");
            goodsNames.add("Панели");
            goodsNames.add("Столб");
            goodsNames.add("Трубы проф. 40х20");
            goodsNames.add("Проволока с терм.");
            goodsNames.add("Проволока оцинк.с терм.");
            goodsNames.add("Калитка");
            goodsNames.add("Засов");
            goodsNames.add("Соединитель проф.трубы");
            goodsNames.add("Заглушки пластиковые");
        }
    }

    public static List<String> getGoodsNames() {
        return goodsNames;
    }

    public static List<String> getGoodsFromInitialFile(String fileName) throws IOException {
        List<String> goodsFromInitialFile = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        ) {
            String string;
            addGoodsNames();
            while ((string = bufferedReader.readLine()) != null) {
                goodsFromInitialFile.add(string);
            }
        }

        return goodsFromInitialFile;
    }

    public static boolean doesStringContainProduct(String string, List<String> productNames) {
        String[] params = string.split(";");
        if (params.length == 1) {
            return false;
        }
        String checkingPart = params[1].substring(1);
        for (String productName : productNames) {
            if (checkingPart.length() < productName.length()) {
                continue;
            }
            if (checkingPart.startsWith(productName)) {
                return true;
            }
        }
        return false;
    }

    public static LocalDateTime getLastDateTimeOfFileDownloading(String fileWithPreviousDownloadTime) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileWithPreviousDownloadTime));
        LocalDateTime lastDateTime = null;
        String lastDateTimeString = br.readLine();
        if (lastDateTimeString != null) {
            lastDateTime = LocalDateTime.of(
                    Integer.parseInt(lastDateTimeString.substring(0, 4)),
                    Integer.parseInt(lastDateTimeString.substring(5, 7)),
                    Integer.parseInt(lastDateTimeString.substring(8, 10)),
                    0,
                    0);
        }

        return lastDateTime;
    }

    public static long getMilliSecondsBetweenNextUpdatingFileDTAndNow() {
        long delay;
        int millisecondsInOneDay = 86_400_000;
        int secondsInHour = 3600;
        int secondsInMinute = 60;
        int delta;
        int baseHour = 6;
        int baseMinute = 30;
        LocalDateTime now = LocalDateTime.now();
        int nowHour = now.getHour();
        int nowMinute = now.getMinute();
        if (nowHour > baseHour || (nowHour == baseHour && nowMinute > baseMinute)) {
            if (baseMinute >= nowMinute) {
                delta = (nowHour - baseHour) * secondsInHour + (nowMinute - baseMinute) * secondsInMinute;
            } else {
                delta = (nowHour - baseHour - 1) * secondsInHour + (nowMinute + 60 - baseMinute) * secondsInMinute;
            }
            delay = millisecondsInOneDay - delta * 1000;
        } else {
            if (baseMinute >= nowMinute) {
                delay = (baseHour - nowHour) * secondsInHour + (baseMinute - nowMinute) * secondsInMinute;
            } else {
                delay = (baseHour - nowHour - 1) * secondsInHour + (baseMinute + 60 - nowMinute) * secondsInMinute;
            }
        }
        return delay;
    }
}
