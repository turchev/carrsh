package com.github.turchev.data.service;

import com.github.turchev.data.entity.Mechanic;
import com.github.turchev.data.repository.MechanicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MechanicService extends CrudService<Mechanic, Long> {

    private final MechanicRepository repository;

    public MechanicService(@Autowired MechanicRepository repository) {
        this.repository = repository;
    }

    @Override
    protected MechanicRepository getRepository() {
        return repository;
    }
}
