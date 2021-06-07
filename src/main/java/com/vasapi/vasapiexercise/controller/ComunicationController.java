package com.vasapi.vasapiexercise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ComunicationController {

    @GetMapping("/")
    public ModelAndView main() {
        ModelAndView maw = new ModelAndView("index");
        String welcome = "Welcome to the VAS api, please, complete the url adding /YYYMMDD to process a file";
        maw.addObject("welcome", welcome);
        return maw;
    }
}
