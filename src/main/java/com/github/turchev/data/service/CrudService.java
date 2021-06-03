package com.github.turchev.data.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

abstract class CrudService<T, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    public Optional<T> get(ID id) {
        return getRepository().findById(id);
    }
    public T update(T entity) {
        return getRepository().save(entity);
    }
    public void delete(ID id) {
        getRepository().deleteById(id);
    }
    public Page<T> list(Pageable pageable) {
        return getRepository().findAll(pageable);
    }
    public int count() {
        return (int) getRepository().count();
    }
    public List<T> findAll() {
        return getRepository().findAll();
    }

}
