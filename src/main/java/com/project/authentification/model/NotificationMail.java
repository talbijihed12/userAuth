package com.project.authentification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMail {
    private String subject;
    private String recipient;
    private String Body;
}
