package org.example.effectivemobilerestapi.repositories;

import org.example.effectivemobilerestapi.entities.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {

        @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.task.id = :taskId")
        List<Comment> findAllByTaskIdWithAuthor(@Param("taskId") Long taskId);

        @EntityGraph(attributePaths = {"author", "task"})
        Optional<Comment> findById(Long id);

}
