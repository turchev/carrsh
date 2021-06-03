package com.github.turchev.data.repository;

import com.github.turchev.data.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {

    List<Order> findUsingFilter(String sqlLikeDescription, String sqlLikeStatus, String sqlLikeClientLastName);

}
