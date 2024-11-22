package com.example.SaunaHat.controller;

import com.example.SaunaHat.controller.form.MessageForm;
import com.example.SaunaHat.controller.form.UserForm;
import com.example.SaunaHat.service.MessageService;
import com.example.SaunaHat.service.UserService;
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

    @Autowired
    HttpSession session;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;


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
     * ログイン処理　ModelAttributeバージョンを念のため残します
     @GetMapping("/loginUser")
    public ModelAndView select(@ModelAttribute(name = "formModel") UserForm userForm){
        UserForm loginUser = userService.selectLoginUser(userForm);
        session.setAttribute("loginUser",loginUser);
        return new ModelAndView("redirect:/home");
    }
     */
    @GetMapping("/loginUser")
    public ModelAndView select(@RequestParam(name = "account") String account,
                               @RequestParam(name = "password") String password){
        UserForm loginUser = userService.selectLoginUser(account, password);
        session.setAttribute("loginUser",loginUser);
        return new ModelAndView("redirect:/home");
    }

    /*
     * ホーム画面・投稿表示処理
     */
    @GetMapping("/home")
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView();

        //投稿の表示
        //投稿を全件取得
        List<MessageForm> messages = messageService.findAllMessage();

        //取得した情報を画面にバインド
        mav.addObject("formModel", messages);

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
     *アカウント停止・復活処理
     */
    @PutMapping("/accountStop/{id}")
    public ModelAndView accountStop(@PathVariable Integer id, @RequestParam(name = "userId")String userId) {
        ModelAndView mav = new ModelAndView();

        //ユーザ復活停止状態を更新

        // ユーザーを全件取得
        List<UserForm> userData = userService.findAllUser();

        //ユーザーデータオブジェクトを保管
        mav.addObject("users", userData);


        //ユーザ管理画面へリダイレクト
        return new ModelAndView("redirect:/userManage");
    }

    /*
     *ログアウト処理
     */
    @GetMapping("/logout")
    public ModelAndView logout() {

        ModelAndView mav = new ModelAndView();
        /*空のFormを作成(ログイン画面への遷移formModel追加バージョンも念のため残します)
        UserForm userForm = new UserForm();

        // Formをバインド先にセット
        mav.addObject("formModel", userForm);
        */

        // セッションの無効化
        session.invalidate();

        /*
        if(session.getAttribute("loginUser") == null){
            System.out.println("ログインユーザーのセッションが破棄されました。");
        }
        */

        //ログイン画面へフォワード処理
        mav.setViewName("/login");
        //return new ModelAndView("./");
        return mav;
    }

}