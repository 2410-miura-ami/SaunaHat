package com.example.SaunaHat.controller;

import com.example.SaunaHat.controller.form.UserForm;
import com.example.SaunaHat.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ForumController {
    //@Autowired
    //MessageService messageService;

    @Autowired
    HttpSession session;

    @Autowired
    UserService userService;

    /*
     * ログイン画面表示処理
     */
    @GetMapping
    public ModelAndView login(){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/login");
        return mav;
    }
    /*
     * ログイン処理
     */
    @GetMapping("/loginUser")
    public ModelAndView select(@RequestParam(name = "account", required = false) String account,
                               @RequestParam(name = "password", required = false) String password){
        //エラーメッセージ空箱
        List<String> errorMessages = new ArrayList<String>();
        //バリデーション
        if (StringUtils.isBlank(account)){
            errorMessages.add("アカウントを入力してください");
        }
        if (StringUtils.isBlank(password)){
            errorMessages.add("パスワードを入力してください");
        }
        //if (!StringUtils.isEmpty(errorMessages.get(0))){
            //ModelAndView mav = new ModelAndView();
            //mav.addObject("errorMessages",errorMessages);
            //return mav;
        //}

        UserForm loginUser = userService.selectLoginUser(account, password);
        session.setAttribute("loginUser",loginUser);
        return new ModelAndView("redirect:/home");
    }

    /*
     * ホーム画面表示処理
     */
    @GetMapping("/home")
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView();

        // 画面遷移先を指定
        mav.setViewName("/home");

        //画面に遷移
        return mav;
    }

    /*
     *ユーザー管理画面表示処理
     */
    @GetMapping("/userManage")
    public ModelAndView userManage() {
        ModelAndView mav = new ModelAndView();

        // ユーザーを全件取得
        List<UserForm> userData = userService.findAllUser();

        //ユーザーデータオブジェクトを保管
        mav.addObject("users", userData);

        //画面遷移先を指定
        mav.setViewName("/user_manage");

        //画面に遷移
        return mav;
    }

    /*
     *ログアウト処理
     */
    @GetMapping("/logout")
    public ModelAndView logout() {

        ModelAndView mav = new ModelAndView();

        // セッションの無効化
        session.invalidate();

        //ログイン画面へフォワード処理
        //mav.setViewName("/");
        //return new ModelAndView("/");
        //return mav;
        return new ModelAndView("redirect:/");
    }

}