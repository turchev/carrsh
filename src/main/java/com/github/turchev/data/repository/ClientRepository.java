package com.github.turchev.data.repository;

import com.github.turchev.data.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client,Long> {

    List<String> getAllLastName();

}
