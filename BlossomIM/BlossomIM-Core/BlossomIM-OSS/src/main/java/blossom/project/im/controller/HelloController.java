package blossom.project.im.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther ZhangBlossom
 */
@RestController
@RequestMapping("f")
public class HelloController {

    // 127.0.0.1:88/a/hello

    @GetMapping("hello")
    public Object hello() {
        return "Hello world~";
    }
}
