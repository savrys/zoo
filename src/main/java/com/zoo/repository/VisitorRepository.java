package com.zoo.repository;

import com.zoo.model.Visitor;
import java.util.List;
import java.util.Optional;

public interface VisitorRepository {
    void save(Visitor visitor);
    Optional<Visitor> findById(int id);
    List<Visitor> findAll();
    void update(Visitor visitor);
    void delete(int id);
}