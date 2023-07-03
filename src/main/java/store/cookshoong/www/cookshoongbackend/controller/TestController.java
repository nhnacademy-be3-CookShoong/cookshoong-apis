package store.cookshoong.www.cookshoongbackend.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping
    public HttpEntity<String> test() {
        return ResponseEntity
            .ok("TEST");
    }
}
