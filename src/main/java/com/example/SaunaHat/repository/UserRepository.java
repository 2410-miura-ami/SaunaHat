package com.example.SaunaHat.repository;

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
public interface UserRepository extends JpaRepository<User, Integer> {
    @Transactional
    @Modifying
    @Query(value = "SELECT * FROM users WHERE account = :account AND password =  :password", nativeQuery = true)
    public List<User> selectUser(@Param("account") String account, @Param("password")String password);

    /*
    @Transactional
    @Query(value = "SELECT m FROM Message m JOIN FETCH m.user u ORDER BY m.createdDate DESC")
    public List<Message> selectMessage();
    */

    //nativeQueryを使った場合
    //ユーザーの取得（ユーザー管理画面）
    @Query(value = "SELECT u.id AS id, u.account AS account, u.password AS password, u.name AS name, u.branch_id AS branch_id, u.department_id AS department_id, u.is_stopped AS is_stopped, b.name AS branchName, d.name AS departmentName ,u.created_date AS created_date, u.updated_date AS updated_date " +
            "FROM users u " +
            "INNER JOIN branches b ON u.branch_id = b.id " +
            "INNER JOIN departments d ON u.department_id = d.id " +
            "ORDER BY u.id ASC",
            nativeQuery = true)
    public List<User> selectUser();

    //アカウント停止状態のみ変更
    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET is_stopped = :isStoppedId, updated_date = CURRENT_TIMESTAMP WHERE id = :userId", nativeQuery = true)
    public void editIsStopped(@Param("isStoppedId") Integer isStoppedId, @Param("userId")Integer userId);
}