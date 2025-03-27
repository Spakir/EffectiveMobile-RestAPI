package org.example.effectivemobilerestapi.repositories;

import org.example.effectivemobilerestapi.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByAuthorEmail(String author, Pageable pageable);

    Page<Task> findByExecutorEmail(String executor, Pageable pageable);

    Optional<Task> findById(Long id);
}
