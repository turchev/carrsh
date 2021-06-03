package com.github.turchev.data.service;

import com.github.turchev.data.entity.Client;
import com.github.turchev.data.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClientService extends CrudService<Client, Long> {

    private final ClientRepository repository;

    public ClientService(@Autowired ClientRepository repository) {
        this.repository = repository;
    }

    public List<String> getAllLastName(){
        return  getRepository().getAllLastName();
    }

    @Override
    protected ClientRepository getRepository() {
        return repository;
    }

}
