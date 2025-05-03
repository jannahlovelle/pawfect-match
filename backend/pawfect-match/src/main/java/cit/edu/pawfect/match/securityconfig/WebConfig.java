package cit.edu.pawfect.match.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class WebConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        
        // Add both localhost (for development) and your deployed frontend (Vercel) here
        config.addAllowedOrigin("http://localhost:3000");  // for local dev
        config.addAllowedOrigin("https://pawfect-match-lilac.vercel.app");  // for deployed frontend

        config.addAllowedHeader("*");
        config.addAllowedMethod("*"); // Allow all HTTP methods (GET, POST, etc.)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
