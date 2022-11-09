package com.project.authentification.exceptions;

import org.springframework.mail.MailException;

public class EmailException extends RuntimeException {
    public EmailException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }
    public EmailException(String exMessage) {
        super(exMessage);
    }
}
