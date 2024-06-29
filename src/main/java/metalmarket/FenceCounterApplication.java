package metalmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

@SpringBootApplication
public class FenceCounterApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(FenceCounterApplication.class, args);


/*		String fileUrl = "https://metal-market.ru/upload/www_tovar.csv";
		URL url = new URL(fileUrl);
		URLConnection urlConnection = url.openConnection();
		try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.))) {
			*//*String inputLine;
			while ((inputLine = bufferedReader.readLine()) != null) {
				System.out.println(inputLine);
			}*//*
			System.out.println(bufferedReader.readLine());
			System.out.println(bufferedReader.readLine());

		}*/


	}



}
