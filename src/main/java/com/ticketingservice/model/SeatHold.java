package com.ticketingservice.model;

import java.io.Serializable;
import java.util.Set;

public class SeatHold implements Serializable {

    private static final long serialVersionUID = -4L;
    
    private int id;    
    private String customerEmail;    
    private Set<Seat> seats;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getCustomerEmail() {
        return customerEmail;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    public Set<Seat> getSeats() {
        return seats;
    }
    public void setSeats(Set<Seat> seats) {
        this.seats = seats;
    }

}
