package metalmarket;

import com.google.gson.Gson;
import metalmarket.util.DataHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class FenceCounterApplication {

    public static void main(String[] args) {
        DataHandler dataHandler = new DataHandler();
        long delay = dataHandler.getMilliSecondsBetweenNextUpdatingFileDTAndNow();
        dataHandler.startHandling(delay);
        SpringApplication.run(FenceCounterApplication.class, args);
    }
}