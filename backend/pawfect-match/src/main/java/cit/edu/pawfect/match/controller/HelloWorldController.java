package cit.edu.pawfect.match.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    
    @GetMapping("/")

    public String print(){
        return "Hello World!";
    }
}
