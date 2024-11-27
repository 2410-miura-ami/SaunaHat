package com.example.SaunaHat.repository;

import com.example.SaunaHat.repository.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    //カテゴリの絞り込みなしの場合の投稿取得
    @Transactional
    @Query(value = "SELECT m FROM Message m JOIN FETCH m.user u " +
            "WHERE m.createdDate BETWEEN :startDate AND :endDate " +
            "ORDER BY m.createdDate DESC")
    public List<Message> selectMessage(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

    //nativeQueryを使った場合
    /*@Query(value = "SELECT m.id AS id, m.title AS title, m.text AS text, m.category AS category, m.user_id AS user_id, u.name AS userName, u.account AS userAccount, m.created_date AS created_date, m.updated_date AS updated_date " +
            "FROM messages m " +
            "INNER JOIN users u ON m.user_id = u.id " +
            "ORDER BY m.created_date DESC limit 100",
            nativeQuery = true)*/

    //カテゴリの絞り込みありの場合の投稿取得
    @Transactional
    @Query(value = "SELECT m FROM Message m JOIN FETCH m.user u " +
            "WHERE m.createdDate BETWEEN :startDate AND :endDate " +
            "AND m.category LIKE :category " +
            "ORDER BY m.createdDate DESC")
    public List<Message> selectMessageByCategory(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("category")String category);
}
