package com.example.SaunaHat.repository;

import com.example.SaunaHat.repository.entity.Comment;
import com.example.SaunaHat.repository.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
