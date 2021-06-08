package com.vasapi.vasapiexercise.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multiset;
import com.vasapi.vasapiexercise.domain.Communication;
import com.vasapi.vasapiexercise.service.CommunicationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/metrics")
public class MetricsController {
    private final CommunicationService communicationService;
    private String ko = "KO";
    private String ok = "OK";

    public MetricsController(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @GetMapping()
    public String mainMethod() {
        Integer missing = missingFields();
        Integer empty = emptyFields();
        Integer wrong = wrongFields() + missing + empty;
        Map<String, Integer> wordsOrdered = mostUsedWords();
        Map<String, Integer> cc = countCallsByCC();
        Map<String, Double> mediaDuration = callAverageDurationByCC();
        Map<String, Double> relation = relationOkKO();
        String wordsJson = fromMapToJson(wordsOrdered);
        String ccJson = fromMapToJson(cc);
        String averageJson = fromMapToJsonD(mediaDuration);
        String relationJson = fromMapToJsonD(relation);
        //we return it in json format
        return "{\"kpis\":{\"missing_fields\":" + missing + ",\"empty_fields\":" + empty + ",\"wrong_fields\":" + wrong + ",\"words_ordered\":" + wordsJson +
                ",\"calls_by_cc\":" + ccJson + ",\"average_duration\":" + averageJson + ",\"relation\":" + relationJson + "}}";
    }

    public String fromMapToJson(Map<String, Integer> a) {
        ObjectMapper objectMapper = new ObjectMapper();
        String mapped;
        try {
            mapped = objectMapper.writeValueAsString(a);
            return mapped;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            mapped = "";
        }
        return mapped;
    }

    public String fromMapToJsonD(Map<String, Double> a) {
        ObjectMapper objectMapper = new ObjectMapper();
        String mapped;
        try {
            mapped = objectMapper.writeValueAsString(a);
            return mapped;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            mapped = "";
        }
        return mapped;
    }

    @GetMapping("/view")
    public ModelAndView viewMethod() {
        ModelAndView maw = new ModelAndView("metrics");
        Integer missing = missingFields();
        Integer empty = emptyFields();
        Map<String, Integer> wordsOrdered = mostUsedWords();
        Map<String, Integer> cc = countCallsByCC();
        Map<String, Double> mediaDuration = callAverageDurationByCC();
        Map<String, Double> relation = relationOkKO();
        //We are supposing here that empty and missing fields makes a row "wrong" too
        Integer wrong = wrongFields() + missing + empty;
        maw.addObject("missing", missing);
        maw.addObject("emptyRows", empty);
        maw.addObject("wrong", wrong);
        maw.addObject("wordsOrder", wordsOrdered);
        maw.addObject("cc", cc);
        maw.addObject("duration", mediaDuration);
        maw.addObject("relation", relation);
        return maw;
    }


    public Integer missingFields() {
        Integer result = 0;
        List<Communication> comu = communicationService.list();
        for (int i = 0; i < comu.size(); i++) {
            Communication a = comu.get(i);
            if (a.getMessage_type() == null) {
                result++;
            } else if (a.getMessage_type().equals("CALL")) {
                if (a.getTimestamp() == null || a.getOrigin() == null || a.getDestination() == null || a.getDuration() == null || a.getStatus_code() == null || a.getStatus_description() == null) {
                    result++;
                }
            } else if (a.getMessage_type().equals("MSG")) {
                if (a.getTimestamp() == null || a.getOrigin() == null || a.getDestination() == null || a.getMessage_content() == null || a.getMessage_status() == null) {
                    result++;
                }
            } else {
                result++;
            }
        }
        return result;
    }


    public Integer emptyFields() {
        Integer result = 0;
        List<Communication> comu = communicationService.list();
        for (int i = 0; i < comu.size(); i++) {
            Communication a = comu.get(i);
            if (a.getMessage_type().equals("")) {
                result++;
            } else if (a.getMessage_type().equals("CALL")) {
                if (a.getStatus_code().equals("") || a.getStatus_description().equals("")) {
                    result++;
                }
            } else if (a.getMessage_type().equals("MSG")) {
                if (a.getMessage_content().equals("") || a.getMessage_status().equals("")) {
                    result++;
                }
            }
        }
        return result;
    }

    public Integer wrongFields() {
        Integer result = 0;
        List<Communication> comu = communicationService.list();
        for (int i = 0; i < comu.size(); i++) {
            Communication a = comu.get(i);
            if (a.getMessage_type().equals("MSG") && !(a.getMessage_status().equals("SEEN") || a.getMessage_status().equals("DELIVERED"))) {
                result++;
            }
            if (a.getMessage_type().equals("CALL") && !(a.getStatus_code().equals("OK") || a.getStatus_code().equals("KO"))) {
                result++;
            }
            //Here we are checking that the phone numbers must have at least 11 digits (We could do it in the model to make it more efficient, but it would give problems inserting data in the ddbb)
            if (a.getDestination() != null && a.getOrigin() != null && (countDigit(a.getDestination()) < 11 || countDigit(a.getOrigin()) < 11)) {
                result++;
            }
        }
        return result;
    }

    public Map<String, Integer> mostUsedWords() {
        List<String> wordsList = communicationService.getMostUsedWords();
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        Map<String, Integer> wordCount = countElements(wordsList);
        //Let's sort the map
        wordCount.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
        return result;
    }

    public Map<String, Integer> countCallsByCC() {
        //We suppose here that all call's og and dt have the same cc
        List<String> countryCodes = communicationService.getAllCcOriginRepeated();
        Map<String, Integer> ccCount = countElements(countryCodes);
        return ccCount;
    }

    public Map<String, Double> callAverageDurationByCC() {
        Map<String, Double> res = new HashMap<>();
        //this returns a map with key: cc and values with all their durations
        ListMultimap<String, Integer> ccDuration = communicationService.getGoodCallsForAverage();
        Multiset<String> keys = ccDuration.keys();
        List<String> keyList = keys.stream().collect(Collectors.toList());
        //Let's iterate for each key and take the average of its values
        for (int f = 0; f < ccDuration.size(); f++) {
            List<Integer> values = ccDuration.get(keyList.get(f));
            Double average = calculateAverage(values);
            //adding the same key with the average
            res.put(keyList.get(f), average);
        }
        return res;
    }

    public Map<String, Double> relationOkKO() {
        //By relation, I understand that the statement is asking for a  % of calls that are Ok and a % that are KO
        //Let's take all the "correct" calls, and take the % of KO calls to calculate the OK.
        List<Communication> com = communicationService.getGoodCallsForStatus();
        Map<String, Double> res = new HashMap<>();
        int total = com.size();
        double koCounter = 0.;
        double okCounter = 0.;
        for (Communication c : com) {
            if (ko.equals(c.getStatus_code())) {
                koCounter++;
            } else if (ok.equals(c.getStatus_code())) {
                okCounter++;
            }
        }
        Double ko = (koCounter / total) * 100;
        double koRounded = (double) Math.round(ko * 100) / 100;
        res.put("KO", koRounded);
        Double ok = (okCounter / total) * 100;
        double okRounded = (double) Math.round(ok * 100) / 100;
        res.put("OK", okRounded);
        return res;

    }

    public Double calculateAverage(List<Integer> a) {
        Double result = 0.;
        for (Integer value : a) {
            result += value;
        }
        return result / a.size();
    }

    static int countDigit(long n) {
        int count = 0;
        while (n != 0) {
            n = n / 10;
            ++count;
        }
        return count;
    }

    public Map<String, Integer> countElements(List<String> a) {
        LinkedHashMap<String, Integer> b = new LinkedHashMap<>();
        for (String word : a) {
            Integer count = b.get(word);
            b.put(word, (count == null) ? 1 : count + 1);
        }
        return b;
    }
}

