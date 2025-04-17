package cit.edu.pawfect.match.securityconfig;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            // Load the service account file from resources
            ClassPathResource resource = new ClassPathResource("firebase-service-account.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Firebase service account file not found in resources", e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize Firebase Admin SDK", e);
        }
    }
}