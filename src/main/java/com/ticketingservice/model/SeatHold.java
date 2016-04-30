package com.ticketingservice.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class SeatHold implements Serializable {

    private static final long serialVersionUID = -4L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "email")
    private String customerEmail;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "seatHold", orphanRemoval = true)
    private Set<Seat> seats;

    private String status;

    @Column(name = "creation_ts")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTs;

    public enum SeatHoldStatus {
        HOLD,
        EXPIRED,
        BOOKED,
        NOT_FOUND
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public void addSeat(Seat seat) {
        if (seat == null) return;
        if (this.getSeats() == null) {
            this.seats = new HashSet<>();
        }
        seat.setSeatHold(this);
        this.seats.add(seat);
    }

    public void addSeat(Set<Seat> seats) {
        if (seats == null) return;
        if (this.getSeats() == null) {
            this.seats = new HashSet<>();
        }
        for (Seat seat : seats) {
            seat.setSeatHold(this);
            this.seats.add(seat);
        }
    }

    public String getStatus() {
        return status;
    }

    public String setStatus(String status) {
        return this.status = status;
    }

    public Date getCreationTs() {
        return creationTs;
    }

    public void setCreationTs(Date creationTs) {
        this.creationTs = creationTs;
    }

    @Override
    public String toString() {
        return "SeatHold: {" +
                "id=" + id +
                ", customerEmail='" + customerEmail + '\'' +
                ", status='" + status + '\'' +
                ", creationTs=" + creationTs +
                ", numOfSeats=" + (this.seats == null ? 0 : this.seats.size()) +
                '}';
    }
}
