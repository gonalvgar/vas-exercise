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
//I've decided to make only one Entity to process the jsons. In first place, I saw it as a 2 child class (CALL and MSG) of a parent class communication with their correspondent enums, but, after seeing that I had
//to insert empty and wrong data into the database for the metrics endpoint (for example), I decided to make it as a really simple class without validators. Validators would have done
//the development easier, faster and much more efficient.
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