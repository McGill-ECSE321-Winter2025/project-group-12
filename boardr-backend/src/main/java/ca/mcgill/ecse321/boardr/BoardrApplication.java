package ca.mcgill.ecse321.boardr;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BoardrApplication {

	public static void main(String[] args) {
		// Load environment variables from .env
        Dotenv dotenv = Dotenv.load();

        // Set system properties for Spring Boot to use
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		
		SpringApplication.run(BoardrApplication.class, args);
	}

}
