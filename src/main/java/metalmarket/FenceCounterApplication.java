package metalmarket;

import metalmarket.util.InputHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

@SpringBootApplication
public class FenceCounterApplication {

    public static void main(String[] args) throws IOException {
        InputHandler inputHandler = new InputHandler();
        long delay = inputHandler.getMilliSecondsBetweenNextUpdatingFileDTAndNow();
        inputHandler.startHandling(delay);
        SpringApplication.run(FenceCounterApplication.class, args);
    }
}