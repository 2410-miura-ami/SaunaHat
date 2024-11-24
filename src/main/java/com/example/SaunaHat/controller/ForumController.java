package com.example.SaunaHat.controller;

import com.example.SaunaHat.controller.form.CommentForm;
import com.example.SaunaHat.controller.form.MessageForm;
import com.example.SaunaHat.controller.form.UserForm;
import com.example.SaunaHat.repository.entity.Comment;
import com.example.SaunaHat.service.CommentService;
import com.example.SaunaHat.service.MessageService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

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

        //ログインユーザの部署チェック
        //セッションからログインユーザ情報を取得
        UserForm userForm = (UserForm)session.getAttribute("loginUser");
        //Formから部署IDを取り出す
        int departmentId = userForm.getDepartmentId();
        //ユーザ情報から取得した部署IDを画面にセット
        mav.addObject("departmentId", departmentId);

        //投稿の表示
        //投稿を全件取得(引数に絞り込み情報をセット)
        List<MessageForm> messages = messageService.findAllMessage(startDate, endDate, category);
        //取得した投稿を画面にバインド
        mav.addObject("formModel", messages);

        //絞り込み情報を画面にセット
        mav.addObject("startDate", startDate);
        mav.addObject("endDate", endDate);
        mav.addObject("category", category);

        //返信の表示
        //返信の全件取得
        List<CommentForm> comments = commentService.findAllComment();
        //取得した返信を画面にバインド
        mav.addObject("comments", comments);

        //セッションからログインユーザ情報取得しバインド
        UserForm loginUser = (UserForm) session.getAttribute("loginUser");
        mav.addObject("loginUser", loginUser);

        //コメントのエラーメッセージチェック
        //セッションからエラーメッセージを取得して、有無をチェック
        String errorMessage = (String)session.getAttribute("errorMessage");
        if(errorMessage != null){
            //エラーメッセージがあった場合はセッションからコメントフォームを取得
            CommentForm commentForm = (CommentForm)session.getAttribute("commentForm");
            //全てのテキストボックスに本文が出てしまうので、空白に置き換える
            commentForm.setText("");

            //エラーメッセージとコメントフォームを画面にセット
            mav.addObject("errorMessage", errorMessage);
            mav.addObject("commentForm", commentForm);

            //エラーメッセージとコメントフォームのセッションを破棄
            session.removeAttribute("errorMessage");
            session.removeAttribute("commentForm");
        }else {
            //エラーメッセージがなかった場合は空のコメントフォームを画面にバインド
            CommentForm commentForm = new CommentForm();
            mav.addObject("commentForm", commentForm);
        }

        /*
        //マップを使ってコメントフォームを投稿ごとに用意
        //マップを準備
        Map<Integer, CommentForm> commentForms = new HashMap<>();
        for (MessageForm message : messages) {
            //コメントフォームを準備
            CommentForm commentForm = new CommentForm();
            //各投稿のIDをコメントフォームにセット
            commentForm.setMessageId(message.getId());
            //マップに投稿IDと投稿IDの入ったコメントフォームをセット
            commentForms.put(message.getId(), commentForm);

        }

        //セッションからエラーメッセージを取得
        String errorMessages = (String)session.getAttribute("errorMessage");
        //エラーメッセージがある場合は、下記の処理
        if(errorMessages != null) {
            //エラーメッセージがあるFormを区別して取得
            CommentForm errorForm = (CommentForm) session.getAttribute("commentForm");
            // エラー時のフォーム内容を更新
            commentForms.put(errorForm.getMessageId(), errorForm);
            mav.addObject("errorMessages", errorMessages);

            //エラーメッセージとコメント本文のセッションを破棄
            session.removeAttribute("errorMessage");
            session.removeAttribute("commentForm");
        }
        //マップを使って用意したコメントフォームを画面にバインド
        mav.addObject("commentForms", commentForms);
        */

        // 画面遷移先を指定
        mav.setViewName("/home");

        //画面に遷移
        return mav;
    }

    /*
     *コメント登録処理
     */
    @PostMapping("/comment/{id}")
    public ModelAndView comment(@PathVariable("id") int messageId, @ModelAttribute("commentForm")CommentForm commentForm, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView();

        //コメント本文のバリデーション
        //エラーメッセージの準備
        String errorMessage = null;
        //コメントフォームから返信の本文を取得
        String text = commentForm.getText();

        //コメントがブランク、500文字を超えた場合にエラーメッセージをセット
        if(StringUtils.isBlank(text)){
            errorMessage = "メッセージを入力してください";
        }else if(text.length() > 500){
            errorMessage = "500文字内で入力してください";
        }
        //エラーメッセージが1つでもあった場合はエラーメッセージとコメント本文を画面にセット
        if(!(errorMessage == null)){
            //返信対象の投稿IDをFormにセット
            commentForm.setMessageId(messageId);
            session.setAttribute("commentForm", commentForm);
            session.setAttribute("errorMessage", errorMessage);

        }else {
            //エラーメッセージが空の場合はコメント投稿処理を実行
            //セッションからユーザIDを取得
            int userId = ((UserForm) session.getAttribute("loginUser")).getId();
            //取得した情報をFormにセット
            commentForm.setMessageId(messageId);
            commentForm.setUserId(userId);

            //投稿のIDを引数にinsertする
            commentService.addComment(commentForm);
        }

        // 画面遷移先を指定
        mav.setViewName("redirect:/home");

        //画面に遷移
        return mav;
    }

    /*
     *コメント削除処理
     */
    @DeleteMapping("/deleteComment/{id}")
    public ModelAndView deleteComment(@PathVariable Integer id) {
        //コメントのIDを引数に削除する
        commentService.deleteComment(id);
        //コメントをテーブルから削除した後、トップ画面へ戻る
        return new ModelAndView("redirect:/home");
    }

    /*
     * 投稿削除処理
     */
    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteContent(@PathVariable Integer id) {
        //投稿を削除する
        messageService.deleteMessage(id);
        //投稿をテーブルから削除した後、トップ画面へ戻る
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
     *ユーザー編集画面表示
     */
    @GetMapping("/editUser/{id}")
    public ModelAndView editUser(@PathVariable Integer id){
        ModelAndView mav = new ModelAndView();

        //編集ユーザー情報を取得
        UserForm editUser = userService.selectEditUser(id);

        //編集するユーザー情報を画面にバインド
        mav.addObject("user", editUser);

        //画面遷移先を指定
        mav.setViewName("/user_edit");

        //画面に遷移
        return mav;
    }

    /*
     *ユーザー編集処理
     */
    @PutMapping("/update/{id}")
    public ModelAndView updateUser(@PathVariable Integer id, @ModelAttribute("user") UserForm userForm, @RequestParam(name="branch") Integer branchId, @RequestParam(name="department") Integer departmentId){
        ModelAndView mav = new ModelAndView();

        //パスワードの入力がない時、パスワード以外を更新

        //パスワードの入力ある時、確認用と一致するかチェック

        //エラーチェック

        //更新処理
        userForm.setId(id);
        userForm.setBranchId(branchId);
        userForm.setDepartmentId(departmentId);
        userService.saveUser(userForm);

        //ユーザー管理画面へリダイレクト
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