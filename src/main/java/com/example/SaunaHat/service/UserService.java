package com.example.SaunaHat.service;

import com.example.SaunaHat.controller.form.UserForm;
import com.example.SaunaHat.repository.UserRepository;
import com.example.SaunaHat.repository.entity.Branch;
import com.example.SaunaHat.repository.entity.Department;
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

        //ログインユーザ情報取得
        List<User> results = userRepository.selectUser(account, password);
        //存在しないアカウントの場合nullを返す
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
            user.setBranchId(result.getBranch().getId());
            user.setDepartmentId(result.getDepartment().getId());
            user.setIsStopped(result.getIsStopped());
            user.setBranchName(result.getBranch().getName());
            user.setDepartmentName(result.getDepartment().getName());
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
        //repositoryを呼び出して、戻り値をEntityとして受け取る
        List<User> results = userRepository.selectUser();
        List<UserForm> users = setUserForm(results);
        return users;
    }

    //アカウント停止・復活変更処理
    public void editIsStopped(Integer isStoppedId, Integer userId){
        userRepository.editIsStopped(isStoppedId, userId);
    }

    /*
     *ユーザー編集画面表示（ユーザー取得）
     */
    public UserForm selectEditUser(Integer id){
        List<User> results = new ArrayList<>();
        results.add(userRepository.findById(id).orElse(null));
        List<UserForm> users = setUserForm(results);
        return users.get(0);
    }

    /*
     *ユーザー編集処理（ユーザー更新）
     */
    public void saveUser(UserForm reqUser) {
        User saveUser = setUserEntity(reqUser);
        userRepository.save(saveUser);
    }

    /*
     * 取得した情報をEntityに設定
     */
    public User setUserEntity(UserForm reqUser) {
        User user = new User();

        //branchIdをBranch型にする
        Branch branch = new Branch();
        branch.setId(reqUser.getBranchId());

        //departmentIdをDepartment型にする
        Department department = new Department();
        department.setId(reqUser.getDepartmentId());

        //Branch型のbranchIdと、Department型のdepartmentId
        user.setId(reqUser.getId());
        user.setAccount(reqUser.getAccount());
        user.setPassword(reqUser.getPassword());
        user.setName(reqUser.getName());
        user.setBranch(branch);
        user.setDepartment(department);

        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(nowDate);
        try {
            user.setUpdatedDate(sdf.parse(currentTime));
            if (reqUser.getCreatedDate() == null) {
                user.setCreatedDate(sdf.parse(currentTime));
            } else {
                user.setCreatedDate(reqUser.getCreatedDate());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return user;
    }
}
