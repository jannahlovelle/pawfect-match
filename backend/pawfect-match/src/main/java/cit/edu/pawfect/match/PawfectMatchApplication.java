package cit.edu.pawfect.match;
 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class PawfectMatchApplication {

	public static void main(String[] args) {

		// Read directly from environment variables set in Render's dashboard
		String mongodbUri = System.getenv("MONGODB_URI");
		String jwtSecret = System.getenv("JWT_SECRET");
		String cloudinaryUrl = System.getenv("CLOUDINARY_URL");

		// Set system properties if other parts of the app depend on them
		if (mongodbUri != null) System.setProperty("MONGODB_URI", mongodbUri);
		if (jwtSecret != null) System.setProperty("JWT_SECRET", jwtSecret);
		if (cloudinaryUrl != null) System.setProperty("CLOUDINARY_URL", cloudinaryUrl);

		SpringApplication.run(PawfectMatchApplication.class, args);

		System.out.println("Spring Boot is working!!");
	}

}