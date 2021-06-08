package com.vasapi.vasapiexercise.domain;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@Entity
public class Communication {

    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private Integer id;
    protected String message_type;
    private Long timestamp;
    private Long origin;
    private Long destination;
    private Integer duration;
    private String status_code;
    private String status_description;
    private String message_content;
    private String message_status;


    public Communication() {

    }
}