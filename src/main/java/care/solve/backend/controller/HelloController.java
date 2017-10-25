package care.solve.backend.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class HelloController {

    @GetMapping
    public String hello() {
        return "Hello, " + SecurityContextHolder.getContext().getAuthentication().getName() + "!";
    }
}
