package metalmarket.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CommonDataHandler {
    private static List<String> goodsNames = new ArrayList<>();

    private CommonDataHandler() {
    }

    public static boolean doesStringContainProduct(String string, List<String> productNames) {
        String[] params = string.split(";");
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

    public static void updateFileWithGoods() throws IOException {
        String fileUrl = "https://metal-market.ru/upload/www_tovar.csv";
        URL url = new URL(fileUrl);
        URLConnection urlConnection = url.openConnection();
        String fileName = "C:\\dev\\Работа\\Расчет заборов\\tovar.csv";
        String fileWithLastUpdateDateTime = "Последняя дата скачивания файла с товаром.txt";
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream(), Charset.forName("windows-1251")));
             BufferedWriter bufferedWriter = new BufferedWriter(
                     new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            String string;
            addGoodsNames();
            while ((string = bufferedReader.readLine()) != null) {
                if (CommonDataHandler.doesStringContainProduct(string, goodsNames)) {
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

    public static void writeDataToFile(List<String> goods) throws IOException {
        String fileName = "C:\\dev\\Работа\\Расчет заборов\\handled_tovar.csv";
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            for (String good : goods) {
                bufferedWriter.write(good);
                bufferedWriter.newLine();
            }
        }
    }

    public static void addGoodsNames() {
        if (goodsNames.isEmpty()) {
            goodsNames.add("Профнастил");
            goodsNames.add("Столб");
            goodsNames.add("Заглушки");
            goodsNames.add("Трубы проф.");
        }
    }

    public static List<String> getGoodsNames() {
        return goodsNames;
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

    public static List<String> getGoodsFromInitialFile(String fileName) throws IOException {
        List<String> goodsFromInitialFile = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        ) {
            String string;

            addGoodsNames();
            while ((string = bufferedReader.readLine()) != null) {
                if (doesStringContainProduct(string, getGoodsNames())) {
                    goodsFromInitialFile.add(string);
                }
            }
        }

        return goodsFromInitialFile;
    }
}
