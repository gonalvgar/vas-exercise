package com.vasapi.vasapiexercise.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vasapi.vasapiexercise.component.NetDao;
import com.vasapi.vasapiexercise.domain.Communication;
import com.vasapi.vasapiexercise.service.CommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;


@Controller
public class ApiController {
    private final CommunicationService communicationService;

    public ApiController(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @Autowired
    NetDao netDao;
    private static String uri = "https://raw.githubusercontent.com/vas-test/test1/master/logs/MCP_";

    @GetMapping("/date/{date}")
    public ModelAndView InsertCommunications(@PathVariable("date") String date) throws Exception {
        ModelAndView maw = new ModelAndView("files");
        //this is to measure the parse json to object time
        try {
            long start = System.currentTimeMillis();
            String rawJson = netDao.request(uri + date + ".json");
            ObjectMapper mapper = new ObjectMapper();
            try {
                List<Communication> communications = mapper.readValue(rawJson, new TypeReference<List<Communication>>() {
                });
                long end = System.currentTimeMillis();
                long time = end - start;
                communicationService.saveAll(communications, time);
                maw.addObject("response", "Communications saved!");
                System.out.println("Communications saved!");
            } catch (Exception e) {
                System.out.println("Unable to save communications, json data seems to be unreadable: " + e.getMessage());
                maw.addObject("response", "Unable to save communications, json data seems to be unreadable");
            }
        } catch (Exception e) {
            System.out.println("Unable to save communications:" + e.getMessage());
            maw.addObject("response", "Unable to save communications.");
        }
        return maw;
    }
}

