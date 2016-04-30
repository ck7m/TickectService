package com.ticketingservice.dao;

import com.ticketingservice.model.SeatHold;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by lva833 on 4/29/16.
 */
@Repository
@Transactional
public class TicketServiceDaoImpl implements TicketServiceDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SeatHold saverUpdate(SeatHold seatHold) {
        if (seatHold.getId() == null) {
            entityManager.persist(seatHold);
        } else {
            entityManager.merge(seatHold);
        }
        return seatHold;
    }

    @Override
    public String confirmBooking(int seatHoldId) {
        SeatHold seatHold;
        try {
            seatHold = entityManager.find(SeatHold.class, seatHoldId);
        } catch (Exception e) {
            return SeatHold.SeatHoldStatus.NOT_FOUND.name();
        }
        if (seatHold == null) return SeatHold.SeatHoldStatus.NOT_FOUND.name();
        if (!SeatHold.SeatHoldStatus.HOLD.name().equals(seatHold.getStatus())) {
            return seatHold.getStatus();
        }
        seatHold.setStatus(SeatHold.SeatHoldStatus.BOOKED.name());
        this.saverUpdate(seatHold);
        return seatHold.getStatus();
    }

    @Override
    @Transactional(readOnly = true)
    public SeatHold getSeatHoldByStatus(int seatHoldId, SeatHold.SeatHoldStatus status) {
        return getSeatHold(seatHoldId, status, true);
    }

    private SeatHold getSeatHold(int seatHoldId, SeatHold.SeatHoldStatus status, boolean fetchSeats) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SeatHold> criteriaQuery = criteriaBuilder.createQuery(SeatHold.class);
        Root<SeatHold> seatHold = criteriaQuery.from(SeatHold.class);
        Predicate andQuery = criteriaBuilder.and(criteriaBuilder.equal(seatHold.get("id"), criteriaBuilder.parameter(Integer.class, "ID")), criteriaBuilder.equal(seatHold.get("status"), criteriaBuilder.parameter(String.class, "STATUS")));
        criteriaQuery.where(andQuery);
        if (fetchSeats) {
            seatHold.fetch("seats");
        }
        Query query = entityManager.createQuery(criteriaQuery);
        query.setParameter("ID", seatHoldId);
        query.setParameter("STATUS", status.name());
        List<SeatHold> resultList = query.getResultList();
        return (resultList == null || resultList.size() == 0) ? null : resultList.get(0);
    }
}
