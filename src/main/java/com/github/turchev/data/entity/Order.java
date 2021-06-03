package com.github.turchev.data.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ORDERS")
@NamedQuery(name = "Order.findUsingFilter",
        query = "SELECT o FROM Order o WHERE LOWER(o.description) LIKE LOWER(?1) AND LOWER(o.status) LIKE LOWER(?2) AND LOWER(o.client.lastName) LIKE LOWER(?3)")

public class Order extends AbstractEntity {

    @Column(name = "DESCRIPTION", length = 5000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "CLIENT_ID")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "MECHANIC_ID")
    private Mechanic mechanic;

    @Column(name = "STATUS", length = 50)
    @Enumerated(EnumType.STRING)
    private OrderStatusType status;

    @Column(name = "DATE_CREATE", columnDefinition = "TIMESTAMP")
    private LocalDateTime dateCreate;

    @Column(name = "COMPLETION_DATE", columnDefinition = "TIMESTAMP")
    private LocalDateTime completionDate;

    @Column(name = "PRICE", columnDefinition = "DECIMAL(12,2)")
    private BigDecimal price;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public OrderStatusType getStatus() {
        return status;
    }

    public void setStatus(OrderStatusType status) {
        this.status = status;
    }

    public LocalDateTime getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(LocalDateTime dateCreate) {
        this.dateCreate = dateCreate;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
