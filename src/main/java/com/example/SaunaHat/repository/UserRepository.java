package com.example.SaunaHat.repository;

import com.example.SaunaHat.repository.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Transactional
    @Modifying
    @Query(value = "SELECT * FROM users WHERE account = :account AND password =  :password", nativeQuery = true)
    public List<User> selectUser(@Param("account") String account, @Param("password")String password);
}
