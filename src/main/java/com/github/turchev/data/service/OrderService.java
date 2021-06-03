package com.github.turchev.data.service;

import com.github.turchev.data.entity.Order;
import com.github.turchev.data.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService extends CrudService<Order, Long> {

    private final OrderRepository repository;

    public OrderService(@Autowired OrderRepository repository) {
        this.repository = repository;
    }

    public List<Order> findUsingFilter(String sqlLikeDescription, String sqlLikeStatus, String sqlLikeClientLastName){
        return repository.findUsingFilter(sqlLikeDescription, sqlLikeStatus, sqlLikeClientLastName);
    }

    @Override
    protected OrderRepository getRepository() {
        return repository;
    }

}
