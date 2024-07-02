package metalmarket.util;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class InputHandler {
    private long MILLISECONDS_IN_ONE_DAY = 86_400_000;

    public void startHandling(long firstDelay) {
        Thread mainThread = new Thread() {
            @SneakyThrows
            @Override
            public void run() {
                handleInputFileAndSave();
                if (firstDelay < MILLISECONDS_IN_ONE_DAY) {
                    Thread.sleep(firstDelay);
                } else {
                    Thread.sleep(MILLISECONDS_IN_ONE_DAY);
                }
            }
        };

        mainThread.start();
    }

    private void handleInputFileAndSave() throws IOException {
        String fileName = "C:\\dev\\Работа\\Расчет заборов\\tovar.csv";
        List<String> goodsFromInitialFile = CommonDataHandler.getGoodsFromInitialFile(fileName);
        List<String> handledGoods = new ArrayList<>();
        handledGoods.add(getTitleString());
        for (String goodFromInitialFile : goodsFromInitialFile) {
            handledGoods.add(getHandledGoods(goodFromInitialFile));
        }
        CommonDataHandler.writeDataToFile(handledGoods);

        System.out.println();
    }

    private String getHandledGoods(String goods) {

        String[] params = goods.substring(1, goods.length() - 1).split("\";\"");
        String[] outputParamsArray = new String[15];
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

            outputParamsArray[11] = params[8];
            outputParamsArray[12] = params[12];
            outputParamsArray[13] = params[14];
            outputParamsArray[14] = params[15];
        }

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

    private String getTitleString() {
        return "Id | Название | Тип | Сечение/Ячейка | Диаметр | Высота/длина C | Высота/длина Т| Ширина С | Ширина Т | " +
                "Толщина | Цвет | Цена С | Цена Т | Наличие С | Наличие Т | Примечание";
    }
}
