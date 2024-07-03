package metalmarket.service;

import lombok.RequiredArgsConstructor;
import metalmarket.util.InputHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryService {

    public List<String> getAnswer() throws IOException {
        List<String> goodsFromInitialFile;
        /*String fileWithPreviousDownloadTime
                = "C:\\dev\\Работа\\Расчет заборов\\Последняя дата скачивания файла с товаром.txt";
        LocalDateTime lastDateTime = InputHandler.getLastDateTimeOfFileDownloading(fileWithPreviousDownloadTime);

        if (lastDateTime == null || lastDateTime.plusDays(1).isBefore(LocalDateTime.now())) {
            InputHandler.updateFileWithGoods();
            System.out.println("Файл с товарами обновлен на сервере");
        } else {
            System.out.println(
                    "Прошло меньше суток с момента обновления csv-файла. Загрузка нового файла пока не требуется");
        }

        String fileName = "C:\\dev\\Работа\\Расчет заборов\\tovar.csv";*/
        String fileName = "C:\\dev\\Работа\\Расчет заборов\\Обработанные товары.csv";
        goodsFromInitialFile = InputHandler.getGoodsFromInitialFile(fileName);
        return goodsFromInitialFile;
    }
}
