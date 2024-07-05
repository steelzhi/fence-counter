package metalmarket.util;

import lombok.NoArgsConstructor;
import metalmarket.dto.WareDto;
import metalmarket.enums.City;
import metalmarket.model.Ware;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@NoArgsConstructor
public class DataHandler {
    private static List<String> waresNames = new ArrayList<>();
    private static Map<String, WareDto> wareDtoMap = new TreeMap<>();
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

    public static Map<String, WareDto> getWareDtoMap() {
        return wareDtoMap;
    }

    private void handleInputFileAndSave() throws IOException {
        updateFileWithWaresOnLocalServer();
        wareDtoMap.clear();
        String fileName = WareFilePath.getPathOfUnhandledFile();
        List<String> unhandledWares = getWaresFromFile(fileName);
        List<List<String>> handledWares = new ArrayList<>();
        addCategoriesAndTitle(handledWares);
        for (String unhandledWare : unhandledWares) {
            WareDto wareDto = getHandledWareDto(unhandledWare);
            if (wareDto == null) {
                continue;
            }
            modifyWareDtoId(wareDto);
            wareDtoMap.put(wareDto.getId(), wareDto);
            String handledWareString = getStringWithAllWareParams(wareDto);
            addHandledWareToList(handledWares, handledWareString, wareDto.getName());
        }
        writeDataToFile(handledWares);

        System.out.println();
    }

    public static void writeDataToFile(List<List<String>> wares) throws IOException {
        String fileName = WareFilePath.getPathOfHandledFile();
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            for (List<String> list : wares) {
                for (String ware : list) {
                    bufferedWriter.write(ware);
                    bufferedWriter.newLine();
                }
                bufferedWriter.newLine();
            }
        }
    }

    private void modifyWareDtoId(WareDto wareDto) {
        String wareDtoName = wareDto.getName();
        if (wareDtoName.startsWith("Профнастил")
                || wareDtoName.startsWith("Штакетник")
                || wareDtoName.startsWith("Сетка рабица")
                || wareDtoName.startsWith("Панель")
        ) {
            wareDto.setId("П-" + wareDto.getId());
        } else if (wareDtoName.startsWith("Столб")) {
            wareDto.setId("Ст-" + wareDto.getId());
        } else if (wareDtoName.startsWith("Труба профильная")
                || wareDtoName.startsWith("Проволока вязальная")
        ) {
            wareDto.setId("Н-" + wareDto.getId());
        } else if (wareDtoName.startsWith("Калитка")
                || wareDtoName.startsWith("Засов")
                || wareDtoName.startsWith("Соединитель проф.трубы")
                || wareDtoName.startsWith("Заглушки")
        ) {
            wareDto.setId("Со-" + wareDto.getId());
        }
    }

    private void addHandledWareToList(List<List<String>> handledWares, String handledWareString, String wareDtoName) {
        if (wareDtoName.startsWith("Профнастил")
                || wareDtoName.startsWith("Штакетник")
                || wareDtoName.startsWith("Сетка рабица")
                || wareDtoName.startsWith("Панель")
        ) {
            handledWares.get(1).add(handledWareString);
        } else if (wareDtoName.startsWith("Столб")) {
            handledWares.get(2).add(handledWareString);
        } else if (wareDtoName.startsWith("Труба профильная")
                || wareDtoName.startsWith("Проволока вязальная")
        ) {
            handledWares.get(3).add(handledWareString);
        } else if (wareDtoName.startsWith("Калитка")
                || wareDtoName.startsWith("Засов")
                || wareDtoName.startsWith("Соединитель проф.трубы")
                || wareDtoName.startsWith("Заглушки")
        ) {
            handledWares.get(4).add(handledWareString);
        }
    }

    private static WareDto getHandledWareDto(String ware) {
        String[] params = ware.substring(1, ware.length() - 1).split("\";\"");
        String[] outputParamsArray = new String[16];
        outputParamsArray[0] = params[0];
        if (params[1].startsWith("Профнастил С21")) {
            return null;
        }

        if (params[1].startsWith("Профнастил") && !params[1].startsWith("Профнастил С21")) {
            outputParamsArray[1] = "Профнастил";
            String[] subParams = params[1].split(";");
            outputParamsArray[2] = subParams[1].trim().split(" ")[0];
            outputParamsArray[5] = params[4].split("х")[1];
            outputParamsArray[6] = params[5].split("х")[1];
            outputParamsArray[7] = params[4].split("х")[0];
            outputParamsArray[8] = params[5].split("х")[0];
            int indexOfZero = subParams[0].indexOf('0');
            outputParamsArray[9] = subParams[0].substring(indexOfZero, indexOfZero + 4);

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
                outputParamsArray[5] = subSubParams[4].substring(0, subSubParams[4].length() - 1);
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

        WareDto wareDto = new WareDto();
        wareDto.setId(outputParamsArray[0]);
        wareDto.setName(outputParamsArray[1]);
        wareDto.setType(outputParamsArray[2]);
        wareDto.setDimension(outputParamsArray[3]);
        wareDto.setDiameter(outputParamsArray[4]);
        if (outputParamsArray[5] != null && !outputParamsArray[5].isBlank()) {
            wareDto.setHeightS(Double.parseDouble(outputParamsArray[5]));
        }
        if (outputParamsArray[6] != null && !outputParamsArray[6].isBlank()) {
            wareDto.setHeightT(Double.parseDouble(outputParamsArray[6]));
        }
        if (outputParamsArray[7] != null && outputParamsArray[7].isBlank()) {
            wareDto.setWidthS(Double.parseDouble(outputParamsArray[7]));
        }
        if (outputParamsArray[8] != null && !outputParamsArray[8].isBlank()) {
            wareDto.setWidthT(Double.parseDouble(outputParamsArray[8]));
        }
        if (outputParamsArray[9] != null && !outputParamsArray[9].isBlank()) {
            wareDto.setThickness(outputParamsArray[9].trim());
        }
        wareDto.setCover(outputParamsArray[10]);
        if (outputParamsArray[11] != null && !outputParamsArray[11].isBlank()) {
            wareDto.setPriceS(Double.parseDouble(outputParamsArray[11]));
        }
        if (outputParamsArray[12] != null && !outputParamsArray[12].isBlank()) {
            try {
                wareDto.setPriceT(Double.parseDouble(outputParamsArray[12]));
            } catch (NumberFormatException e) {
                System.out.println();
            }
        }
        if (outputParamsArray[13].equals("1")) {
            wareDto.setAvailabilityS(true);
        } else {
            wareDto.setAvailabilityS(false);
        }
        if (outputParamsArray[14].equals("1")) {
            wareDto.setAvailabilityT(true);
        } else {
            wareDto.setAvailabilityT(false);
        }
        wareDto.setComment(outputParamsArray[15]);
        wareDtoMap.put(wareDto.getId(), wareDto);

        return wareDto;
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

    private String getStringWithAllWareParams(WareDto wareDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(wareDto.getId());
        sb.append("; ");
        sb.append(wareDto.getName());
        sb.append("; ");
        if (wareDto.getType() != null) {
            sb.append(wareDto.getType());
        }
        sb.append("; ");
        if (wareDto.getDimension() != null) {
            sb.append(wareDto.getDimension());
        }
        sb.append("; ");
        if (wareDto.getDiameter() != null) {
            sb.append(wareDto.getDiameter());
        }
        sb.append("; ");
        if (wareDto.getHeightS() != 0) {
            sb.append(wareDto.getHeightS());
        }
        sb.append("; ");
        if (wareDto.getHeightT() != 0) {
            sb.append(wareDto.getHeightT());
        }
        sb.append("; ");
        if (wareDto.getWidthS() != 0) {
            sb.append(wareDto.getWidthS());
        }
        sb.append("; ");
        if (wareDto.getWidthT() != 0) {
            sb.append(wareDto.getWidthT());
        }
        sb.append("; ");
        sb.append(wareDto.getThickness());
        sb.append("; ");
        if (wareDto.getCover() != null) {
            sb.append(wareDto.getCover());
        }
        sb.append("; ");
        if (wareDto.getPriceS() != 0) {
            sb.append(wareDto.getPriceS());
        }
        sb.append("; ");
        if (wareDto.getPriceT() != 0) {
            sb.append(wareDto.getPriceT());
        }
        sb.append("; ");
        if (wareDto.isAvailabilityS()) {
            sb.append("1");
        } else {
            sb.append("0");
        }
        sb.append("; ");
        if (wareDto.isAvailabilityT()) {
            sb.append("1");
        } else {
            sb.append("0");
        }
        sb.append("; ");
        if (wareDto.getComment() != null) {
            sb.append(wareDto.getComment());
        } else {
            sb.append("");
        }
        sb.append(";");

        return sb.toString();
    }

    public static void addWaresNames() {
        if (waresNames.isEmpty()) {
            waresNames.add("Профнастил");
            waresNames.add("Штакетник");
            waresNames.add("Сетка стальная плет.");
            waresNames.add("Сетка оц. плет.");
            waresNames.add("Сетка плетен. полимер.");
            waresNames.add("Панели");
            waresNames.add("Столб");
            waresNames.add("Трубы проф. 40х20");
            waresNames.add("Проволока с терм.");
            waresNames.add("Проволока оцинк.с терм.");
            waresNames.add("Калитка");
            waresNames.add("Засов");
            waresNames.add("Соединитель проф.трубы");
            waresNames.add("Заглушки пластиковые");
        }
    }

    public static List<String> getWaresFromFile(String fileName) throws IOException {
        List<String> wareFromFile = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))
        ) {
            String string;
            addWaresNames();
            while ((string = bufferedReader.readLine()) != null) {
                wareFromFile.add(string);
            }
        }

        return wareFromFile;
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

    public static void setTotalQuantity(WareDto wareDto, Ware ware, City city) {
        if (wareDto.getName().equals("Труба профильная")) {
            double length = 0;
            if (city == City.SAMARA) {
                length = wareDto.getHeightS();
            } else if (city == City.TOLYATTI) {
                length = wareDto.getHeightT();
            }
            double totalLength = ((int) (ware.getQuantity() / length) + 1) * length;
            wareDto.setQuantity(totalLength);
        } else {
            wareDto.setQuantity(ware.getQuantity());
        }
    }

    public static void updateFileWithWaresOnLocalServer() throws IOException {
        String fileUrl = WareFilePath.getUrlOfUnhandledFile();
        URL url = new URL(fileUrl);
        URLConnection urlConnection = url.openConnection();
        String fileName = "C:\\dev\\Работа\\Расчет заборов\\Необработанные товары.csv";
        String fileWithLastUpdateDateTime = "Последняя дата скачивания файла с товаром.txt";
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(urlConnection.getInputStream(), Charset.forName("windows-1251")));
             BufferedWriter bufferedWriter = new BufferedWriter(
                     new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))) {
            String string;
            addWaresNames();
            while ((string = bufferedReader.readLine()) != null) {
                if (doesStringContainWare(string, waresNames)) {
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

    public static boolean doesStringContainWare(String string, List<String> waresNames) {
        String[] params = string.split(";");
        if (params.length == 1) {
            return false;
        }
        String checkingPart = params[1].substring(1);
        for (String wareName : waresNames) {
            if (checkingPart.length() < wareName.length()) {
                continue;
            }
            if (checkingPart.startsWith(wareName)) {
                return true;
            }
        }
        return false;
    }



/*    public static LocalDateTime getLastDateTimeOfFileDownloading(String fileWithPreviousDownloadTime) throws IOException {
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
    }*/
}
