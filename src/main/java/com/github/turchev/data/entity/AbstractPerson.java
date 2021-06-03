package com.github.turchev.data.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractPerson extends AbstractEntity {

    @Column(name = "LAST_NAME",length = 40)
    protected String lastName;

    @Column(name = "FIRST_NAME",length = 40)
    protected String firstName;

    @Column(name = "PATRONYMIC",length = 40)
    protected String patronymic;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Override
    public String toString(){
        return lastName +  " " + firstName.charAt(0) + ". " + patronymic.charAt(0) + ".";
    }
}
