package com.github.turchev.data.repository;

import com.github.turchev.data.entity.Mechanic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface MechanicRepository extends JpaRepository<Mechanic,Long> {
}
