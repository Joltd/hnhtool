package com.evgenltd.hnhtool.harvester.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/error")
    public Response<Void> error() {
        return new Response<>("Test");
    }

    @GetMapping("/success")
    public Response<Void> success() {
        return new Response<>((Void) null);
    }

}
