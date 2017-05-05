package com.bbva.caas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class IndexController {

    @RequestMapping("/")
    public String home(Map<String, Object> model) {
        model.put("message", "Cagate Lorito!!");
        model.put("name", "Samuelito");
        return "index";
    }

    @RequestMapping("/next")
    public String next(Map<String, Object> model) {
        model.put("message", "Samuelitooooooo se está liandoooooooooooooo");
        return "next";
    }

}
