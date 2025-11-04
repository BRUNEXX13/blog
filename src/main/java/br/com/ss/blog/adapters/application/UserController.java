package br.com.ss.blog.adapters.application;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/v1")
public class UserController {

    @GetMapping("/user")
    public String hello() {
        return "Hello, User!";

    }
}
