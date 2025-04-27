package cit.edu.pawfect.match.media;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/cloudinary")
public class CloudinaryTestController {
    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/config")
    public String testConnection() {
        return "Cloudinary Config: " + cloudinary.config.cloudName;
    }
}