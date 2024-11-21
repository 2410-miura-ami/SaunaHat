package com.example.SaunaHat.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class ForumController {
    //@Autowired
    //MessageService messageService;

    /*
     * ログイン画面表示処理？
     */

    /*
     * ログイン処理？
     */

    /*
     * ホーム画面表示処理
     */
    @GetMapping
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView();

        // 画面遷移先を指定
        mav.setViewName("/home");

        //画面に遷移
        return mav;
    }
}