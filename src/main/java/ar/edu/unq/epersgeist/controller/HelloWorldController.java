package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.configuration.TeamProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloWorldController {

    private final TeamProperties teamProperties;

    @GetMapping("/")
    public String helloWorld() {
        return "Hello World! " + teamProperties.getName();
    }
}
