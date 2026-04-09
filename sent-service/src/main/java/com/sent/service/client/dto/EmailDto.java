package com.sent.service.client.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {

    private Long uid;
    private String from;
    private String to;
    private String subject;
    private String sentDate;
    private String body;
    private boolean seen;
}
