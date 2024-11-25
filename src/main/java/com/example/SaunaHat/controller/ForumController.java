package com.example.SaunaHat.controller;

import com.example.SaunaHat.controller.form.CommentForm;
import com.example.SaunaHat.controller.form.MessageForm;
import com.example.SaunaHat.controller.form.UserForm;
import com.example.SaunaHat.service.CommentService;
import com.example.SaunaHat.service.MessageService;
import com.example.SaunaHat.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class ForumController {

    @Autowired
    HttpSession session;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;


    /*
     * ログイン画面表示処理
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
        //バリデーション
        List<String> errorMessages = new ArrayList<String>();
        if (StringUtils.isBlank(account)){
            errorMessages.add("アカウントを入力してください");
        }
        if (StringUtils.isBlank(password)){
            errorMessages.add("パスワードを入力してください");
        }
        if (errorMessages.size() != 0){
            ModelAndView mav = new ModelAndView();
            mav.addObject("errorMessages", errorMessages);
            mav.setViewName("/login");
            return mav;
        }
        //ログインユーザ情報取得処理
        UserForm loginUser = userService.selectLoginUser(account, password);
        //バリデーション　ユーザが存在しないか停止中ならログイン失敗
        if (loginUser == null || loginUser.getIsStopped() == 1){
            errorMessages.add("ログインに失敗しました");
        }
        if (errorMessages.size() != 0){
            ModelAndView mav = new ModelAndView();
            mav.addObject("errorMessages", errorMessages);
            mav.setViewName("/login");
            return mav;
        }
        //セッションにログインユーザ情報を詰める
        session.setAttribute("loginUser",loginUser);
        return new ModelAndView("redirect:/home");
    }

    /*
     * ホーム画面・投稿表示処理
     */
    @GetMapping("/home")
    public ModelAndView home(@RequestParam(required = false)String startDate, @RequestParam(required = false)String endDate, @RequestParam(required = false)String category) {
        ModelAndView mav = new ModelAndView();

        //投稿の表示
        //投稿を全件取得(引数に絞り込み情報をセット)
        List<MessageForm> messages = messageService.findAllMessage(startDate, endDate, category);
        //返信の表示
        List<CommentForm> comments = commentService.findAllComment();
        //絞り込み情報を画面にセット
        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        mav.addObject("category", category);

        //取得した情報を画面にバインド
        mav.addObject("formModel", messages);
        mav.addObject("comments", comments);

        // 画面遷移先を指定
        mav.setViewName("/home");

        //画面に遷移
        return mav;
    }

    /*
     * 新規投稿画面表示
     */
    @GetMapping("/new")
    public ModelAndView newMessage() {
        ModelAndView mav = new ModelAndView();
        // 空のformを準備
        MessageForm messageForm = new MessageForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", messageForm);
        return mav;
    }

    /*
     *新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addTasks(@ModelAttribute("formModel") @Validated MessageForm messageForm, BindingResult result,
                                 RedirectAttributes redirectAttributes, Model model){
        //入力されたタスク内容を取得
        String title = messageForm.getTitle();
        String category = messageForm.getCategory();
        String text = messageForm.getText();
        //エラーメッセージの準備
        List<String> errorMessages = new ArrayList<>();
        //タスク内容がブランクの場合
        if (title.isBlank()) {
            // エラーメッセージをセット
            errorMessages.add("・件名を入力してください");
        }
        if (category.isBlank()) {
            // エラーメッセージをセット
            errorMessages.add("・カテゴリを入力してください");
        }
        if (text.isBlank()) {
            // エラーメッセージをセット
            errorMessages.add("・本文を入力してください");
        }
        for (FieldError error : result.getFieldErrors()){
            String message = error.getDefaultMessage();
            //取得したエラーメッセージをエラーメッセージのリストに格納
            errorMessages.add(message);
        }
        if(!errorMessages.isEmpty()) {
            model.addAttribute("errorMessages", errorMessages);
            return new ModelAndView("/new");
        }
        //セッションからログイン情報取得
        UserForm loginUser = (UserForm) session.getAttribute("loginUser");
        // 投稿をテーブルに格納
        messageService.saveMessage(messageForm, loginUser);
        // rootへリダイレクト
        return new ModelAndView("redirect:/home");
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
    @GetMapping("/accountStop/{isStoppedId}")
    public ModelAndView accountStop(@PathVariable Integer isStoppedId, @RequestParam(name = "userId")Integer userId) {
        ModelAndView mav = new ModelAndView();

        if(isStoppedId == 0) {
            isStoppedId = 1;
        } else if (isStoppedId == 1) {
            isStoppedId = 0;
        }
        //ユーザ復活停止状態を更新
        userService.editIsStopped(isStoppedId, userId);

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