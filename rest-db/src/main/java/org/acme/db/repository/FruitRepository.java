package org.acme.db.repository;

import java.util.List;

import org.acme.db.model.Fruit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FruitRepository extends JpaRepository<Fruit, Long> {
  List<Fruit> findByNameContaining(String name);   
}
