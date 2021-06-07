package com.vasapi.vasapiexercise.controller;

import com.vasapi.vasapiexercise.service.CommunicationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kpis")
public class KpisController {
    //We're using the same service as metrics because it has the same domain entity
    private final CommunicationService communicationService;

    public KpisController(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @GetMapping("")
    public String mainMethod() {
        Integer rows = numberOfRows();
        Integer calls = numberOfCalls();
        Integer msg = numberOfMessages();
        Integer origin = numberOfCcsOrigin();
        Integer destination = numberOfCcsDestination();
        Integer filesProcessed = numberOfFilesProcessed();
        List<Long> time = timeProcessing();
        //we return it in format json
        return "{\"kpis\":{\"rows\":" + rows + ",\"calls\":" + calls + ",\"messages\":" + msg + ",\"origin_ccs\":" + origin +
                ",\"destination_ccs\":" + destination + ",\"files_processed\":" + filesProcessed + ",\"time_processing_ms\":" + time + "}}";
    }

    @GetMapping("/view")
    public ModelAndView viewMethod() {
        ModelAndView maw = new ModelAndView("kpis");
        Integer rows = numberOfRows();
        Integer calls = numberOfCalls();
        Integer msg = numberOfMessages();
        Integer origin = numberOfCcsOrigin();
        Integer destination = numberOfCcsDestination();
        Integer filesProcessed = numberOfFilesProcessed();
        List<Long> time = timeProcessing();
        maw.addObject("rows", rows);
        maw.addObject("calls", calls);
        maw.addObject("msg", msg);
        maw.addObject("origin", origin);
        maw.addObject("files", filesProcessed);
        maw.addObject("time", time);
        return maw;
    }

    public Integer numberOfRows() {
        return communicationService.list().size();
    }

    public Integer numberOfCalls() {
        return communicationService.getAllCalls().size();
    }

    public Integer numberOfMessages() {
        return communicationService.getAllMessages().size();
    }

    public Integer numberOfCcsOrigin() {
        //We can reuse this method from the service cause it returns as all the ccs existing in the key!
        return communicationService.getGoodCallsForAverage().keySet().size();
    }

    public Integer numberOfCcsDestination() {
        Integer res = communicationService.getAllCcDestinationNotRepeated().elementSet().size();
        System.out.println(res);
        System.out.println(communicationService.getAllCcDestinationNotRepeated());
        return res;
    }

    public Integer numberOfFilesProcessed() {
        return communicationService.getFilesCounter();
    }

    public List<Long> timeProcessing() {
        return communicationService.getTimesFilesProcessed();
    }

}
