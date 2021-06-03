package com.github.turchev.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "CLIENT")
@NamedQuery(name = "Client.getAllLastName",
        query = "SELECT c.lastName FROM Client c")

public class Client extends AbstractPerson {

    @Column(name = "PHONE",length = 40)
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
