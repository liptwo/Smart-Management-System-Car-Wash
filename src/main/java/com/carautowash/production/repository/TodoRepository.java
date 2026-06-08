package com.carautowash.production.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carautowash.production.entity.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
  Optional<Todo> findFirstByTitle(String title);
  void deleteByTitle(String title);
}
