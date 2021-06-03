package com.github.turchev.data.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "MECHANIC")
//@NamedQuery(name = "Mechanic.getTest", query = "SELECT c FROM Mechanic c")
public class Mechanic extends AbstractPerson {

    @Column(name = "WAGES", columnDefinition="DECIMAL(12,2)")
    private BigDecimal wages;

    @OneToMany(mappedBy = "mechanic", fetch = FetchType.EAGER)
    private List<Order> orders;

    public BigDecimal getWages() {
        return wages;
    }

    public void setWages(BigDecimal wages) {
        this.wages = wages;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Integer getOrderSum() {
        return getOrders().size();
    }

    public BigDecimal getPriceSum() {
        BigDecimal priceSum = BigDecimal.ZERO;
        for (Order itrOrder : getOrders()) {
            BigDecimal priceTemp = itrOrder.getPrice();
            priceSum = priceTemp.add(priceSum);
        }
        return priceSum;
    }
}
