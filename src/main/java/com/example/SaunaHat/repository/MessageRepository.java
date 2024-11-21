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

    @Transactional
    @Query(value = "SELECT m FROM Message m JOIN FETCH m.user u ORDER BY m.createdDate DESC")
    public List<Message> selectMessage();

}
