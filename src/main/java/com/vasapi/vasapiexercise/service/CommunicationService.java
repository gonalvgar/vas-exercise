package com.vasapi.vasapiexercise.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multiset;
import com.vasapi.vasapiexercise.domain.Communication;
import com.vasapi.vasapiexercise.repository.CommunicationRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CommunicationService {
    private final String ko = "KO";
    private final String ok = "OK";
    private final CommunicationRepository communicationRepository;
    private static final AtomicInteger count = new AtomicInteger(0);
    private static final List<Long> times = new ArrayList<>();

    public CommunicationService(CommunicationRepository communicationRepository) {
        this.communicationRepository = communicationRepository;
    }

    public List<Communication> list() {
        return (List<Communication>) communicationRepository.findAll();
    }

    public Communication save(Communication communication) {
        return communicationRepository.save(communication);
    }

    public void saveAll(List<Communication> communicationList, long timeProcess) {
        //We're counting here how many files we've processed to insert into the ddbb for the kpis service
        count.incrementAndGet();
        //And collecting in time how much time the system took to process this file
        times.add(timeProcess);
        //We delete all data before inserting to ensure that we're only processing 1 jsp file
        communicationRepository.deleteAll();
        communicationRepository.saveAll(communicationList);
        //minus 1 to discard the last call made by the controller

    }


    //METHODS USED BY METRICS
    public List<String> getMostUsedWords() {
        List<String> result = new ArrayList<>();
        List<String> listWithoutFilters = communicationRepository.getOrderedWordsByOccurrence();
        listWithoutFilters.removeAll(Arrays.asList("", null));
        //We're passing the controller a correct list without any empty or null description and no numbers or characters that we don't want
        for (int i = 0; i < listWithoutFilters.size(); i++) {
            String phrase = listWithoutFilters.get(i);
            String withoutNumbers = phrase.replaceAll("[0-9]", "");
            String withoutPoints = withoutNumbers.replaceAll("\\.", "");
            String def = withoutPoints.replaceAll("\\?", "");
            //let's check if it has more than 1 word:
            if (def.contains(" ")) {
                List<String> both = Arrays.stream(def.split(" ")).collect(Collectors.toList());
                result.addAll(both);
            } else {
                result.add(def);
            }
        }
        result.removeAll(Arrays.asList("", null));

        return result;
    }

    public List<Communication> getGoodCallsForStatus() {
        List<Communication> unprocessed = communicationRepository.getCalls();
        List<Communication> res = new ArrayList<>();
        for (Communication call : unprocessed) {
            if (ok.equals(call.getStatus_code()) || ko.equals(call.getStatus_code())) {
                res.add(call);
            }
        }
        return res;
    }

    public List<String> getAllCcOriginRepeated() {
        List<Long> origins = communicationRepository.getAllOrigins();
        //removing all empty and null fields from our list
        origins.removeAll(Arrays.asList(" ", null));
        return getCcFromList(origins);
    }

    public ListMultimap<String, Integer> getGoodCallsForAverage() {

        List<Communication> calls = communicationRepository.getCalls();
        ListMultimap<String, Integer> res = getMapWithCcs(calls, true, false);
        return res;
    }


    //METHODS USED BY KPIS

    public Multiset<String> getAllCcDestinationNotRepeated() {
        List<Communication> destination = communicationRepository.getAll();
        ListMultimap<String, Integer> a = getMapWithCcs(destination, false, false);
        return a.keys();
    }

    public List<Communication> getAllCalls() {
        return communicationRepository.getCalls();
    }

    public List<Communication> getAllMessages() {
        return communicationRepository.getMessages();
    }

    public Integer getFilesCounter() {
        return count.get();
    }

    public List<Long> getTimesFilesProcessed() {
        return times;
    }

    //UTILITIES
    public List<String> getCcFromList(List<Long> c) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < c.size(); i++) {
            Long a = c.get(i);
            //We're taking the first two numbers as cc
            String cc = a.toString().substring(0, 2);
            result.add(cc);
        }
        return result;
    }

    public ListMultimap<String, Integer> getMapWithCcs(List<Communication> calls, boolean call, boolean origin) {
        ListMultimap<String, Integer> res = ArrayListMultimap.create();
        if (call) {
            for (Communication it : calls) {
                if (it.getDuration() != null && it.getOrigin() != null) {
                    String lcc = it.getOrigin().toString().substring(0, 2);
                    res.put(lcc, it.getDuration());
                }
            }
        } else {
            if (origin) {
                for (Communication it : calls) {
                    if (it.getTimestamp() != null && it.getOrigin() != null) {
                        String lcc = it.getOrigin().toString().substring(0, 2);
                        //We assume that we can store all digits of timestamp in int
                        res.put(lcc, it.getTimestamp().intValue());
                    }
                }
            } else {
                for (Communication it : calls) {
                    if (it.getTimestamp() != null && it.getDestination() != null) {
                        String lcc = it.getDestination().toString().substring(0, 2);
                        //We assume that we won't receive an overflow by parsing it to int, anyway, let's take the 2 first numbers and store them, to secure that we won't get any problems
                        res.put(lcc, Integer.parseInt(it.getTimestamp().toString().substring(0, 2)));
                    }
                }
            }

        }
        return res;
    }
}
