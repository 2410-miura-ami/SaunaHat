package com.example.SaunaHat.service;

import com.example.SaunaHat.controller.form.UserForm;
import com.example.SaunaHat.repository.UserRepository;
import com.example.SaunaHat.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public UserForm selectLoginUser(String account, String password){
        // パスワード暗号化
        //String encPassword = CipherUtil.encrypt(password);
        List<User> results = userRepository.selectUser(account, password);
        if (results.size() == 0) {
            return null;
        }
        //フォームに詰める
        List<UserForm> users = setUserForm(results);
        return users.get(0);
    }

    private List<UserForm> setUserForm(List<User> results) {
        List<UserForm> users = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            UserForm user = new UserForm();
            User result = results.get(i);
            user.setId(result.getId());
            user.setAccount(result.getAccount());
            user.setPassword(result.getPassword());
            user.setName(result.getName());
            user.setBranchId(result.getBranchId());
            user.setDepartmentId(result.getDepartmentId());
            user.setIsStopped(result.getIsStopped());
            user.setCreatedDate(result.getCreatedDate());
            user.setUpdatedDate(result.getUpdatedDate());
            users.add(user);
        }
        return users;
    }

    /*
     *ユーザー管理画面表示（ユーザー取得）
     */
    public List<UserForm> findAllUser(){
        List<User> results = userRepository.findAll();
        List<UserForm> users = setUserForm(results);
        return users;
    }

}
