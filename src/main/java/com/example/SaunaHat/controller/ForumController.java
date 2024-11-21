package com.example.SaunaHat.controller;

import com.example.SaunaHat.controller.form.UserForm;
import com.example.SaunaHat.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
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
    @Autowired
    HttpSession session;
    @Autowired
    UserService userService;

    //MessageService messageService;

    /*
     * ログイン画面表示処理？
     */
    @GetMapping
    public ModelAndView login(){
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        UserForm userForm = new UserForm();
        // 画面遷移先を指定
        mav.setViewName("/login");
        // 準備した空のFormを保管
        mav.addObject("formModel", userForm);
        //エラーメッセージ表示
        List<String> errorMessage = (List<String>) session.getAttribute("errorMessages");
        if (errorMessage != null){
            mav.addObject("errorMessages", errorMessage);
            session.invalidate();
        }
        return mav;
    }
    /*
     * ログイン処理
     */
    @GetMapping("/loginUser")
    public ModelAndView select(@ModelAttribute(name = "formModel") UserForm userForm){
        //エラーメッセージ空箱
        List<String> errorMessages = new ArrayList<String>();
        //バリデーション
        if (StringUtils.isBlank(userForm.getAccount())){
            errorMessages.add("アカウントを入力してください");
        }
        if (StringUtils.isBlank(userForm.getPassword())){
            errorMessages.add("パスワードを入力してください");
        }
        if (!StringUtils.isEmpty(errorMessages.get(0))){
            ModelAndView mav = new ModelAndView();
            mav.addObject("errorMessages",errorMessages);
            return mav;
        }

        UserForm loginUser = userService.selectLoginUser(userForm);
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
}