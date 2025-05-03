package cit.edu.pawfect.match;
 
import io.github.cdimascio.dotenv.Dotenv;  // Import dotenv
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication
public class PawfectMatchApplication {
 
    public static void main(String[] args) {
        // Load .env file if it exists (for local development)
        Dotenv dotenv = Dotenv.configure().load();
 
        // Read from the .env file locally or use the environment variables in Render
        String mongodbUri = System.getenv("MONGODB_URI") != null ? System.getenv("MONGODB_URI") : dotenv.get("MONGODB_URI");
        String jwtSecret = System.getenv("JWT_SECRET") != null ? System.getenv("JWT_SECRET") : dotenv.get("JWT_SECRET");
        String cloudinaryUrl = System.getenv("CLOUDINARY_URL") != null ? System.getenv("CLOUDINARY_URL") : dotenv.get("CLOUDINARY_URL");
 
        // Set system properties if the variables exist
        if (mongodbUri != null) System.setProperty("MONGODB_URI", mongodbUri);
        if (jwtSecret != null) System.setProperty("JWT_SECRET", jwtSecret);
        if (cloudinaryUrl != null) System.setProperty("CLOUDINARY_URL", cloudinaryUrl);
 
        // Start the Spring Boot application
        SpringApplication.run(PawfectMatchApplication.class, args);
 
        System.out.println("Spring Boot is working!!");
    }
}