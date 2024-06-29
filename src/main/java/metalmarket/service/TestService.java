package metalmarket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {
    private LocalDateTime dateTimeOfPreviousUpdate;

    public List<String> getAnswer() throws IOException {
        if (dateTimeOfPreviousUpdate == null || dateTimeOfPreviousUpdate.plusDays(1).isBefore(LocalDateTime.now())) {
            List<String> elements = new ArrayList<>();
            String fileUrl = "https://metal-market.ru/upload/www_tovar.csv";
            URL url = new URL(fileUrl);
            URLConnection urlConnection = url.openConnection();
            String fileName = "C:\\dev\\Работа\\Расчет заборов\\tovar.csv";
            try(BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), Charset.forName("windows-1251")));
                BufferedWriter bufferedWriter =
                        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8))
            ) {
            /*String string = null;
            while ((string = bufferedReader.readLine()) != null) {
                elements.add(string + "\n");
            }*/
                for (int i = 0; i < 4; i++) {
                    String s = bufferedReader.readLine();
                    elements.add(s);
                    bufferedWriter.write(s);
                    bufferedWriter.newLine();
                }
            }

            dateTimeOfPreviousUpdate = LocalDateTime.now();
            return elements;
        } else {
            System.out.println("Прошло меньше суток с момента обновления csv-файла. Загрузка нового файла пока не требуется");
            return null;
        }
    }
}
