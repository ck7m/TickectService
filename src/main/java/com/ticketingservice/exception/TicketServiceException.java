package com.ticketingservice.exception;

/**
 * General Exception class to handle application exceptions.
 *
 * Created by Chandramohan on 4/29/16.
 */
public class TicketServiceException extends Exception {
    public TicketServiceException(String message) {
        super(message);
    }

    public TicketServiceException() {
    }
}
