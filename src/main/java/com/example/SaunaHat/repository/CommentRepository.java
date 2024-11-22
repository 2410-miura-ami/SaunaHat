package com.example.SaunaHat.repository;

import com.example.SaunaHat.repository.entity.Comment;
import com.example.SaunaHat.repository.entity.Message;
import com.example.SaunaHat.repository.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    //コメント全件表示
    /*メモ
    @Query(value = "SELECT c.id AS id, c.text AS text, c.message_id AS message_id, u.name AS name, u.account AS account " +
            "FROM comments c " +
            "INNER JOIN users u ON c.user_id = u.id " +
            "ORDER BY c.created_date ASC ", nativeQuery = true)
     */
    @Transactional
    @Modifying
    @Query(value = "SELECT c FROM Comment c JOIN FETCH c.user u " +
            "ORDER BY c.createdDate ASC")
    public List<Comment> findAllComment();
}
