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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
        if (account.isBlank()){
            errorMessages.add("・アカウントを入力してください");
        }
        if (password.isBlank()){
            errorMessages.add("・パスワードを入力してください");
        }
        if (errorMessages.size() != 0){
            ModelAndView mav = new ModelAndView();
            mav.addObject("errorMessages", errorMessages);
            mav.setViewName("/login");
            return mav;
        }
        //ログインユーザ情報取得処理
        UserForm loginUser = userService.selectLoginUser(account);
        //バリデーション　ユーザが存在しないか停止中ならログイン失敗
        if (loginUser == null || loginUser.getIsStopped() == 1 || !BCrypt.checkpw(password, loginUser.getPassword())){
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

        //管理者権限フィルターのエラーメッセージ表示
        //セッションから管理者権限のエラーメッセージを取得
        String managementErrorMessage = (String) session.getAttribute("managementErrorMessage");
        if(managementErrorMessage != null){
            //管理者権限のエラーメッセージがあった場合は画面にエラーメッセージをセット
            mav.addObject("managementErrorMessage", managementErrorMessage);
            //セッションからエラーメッセージを除去
            session.removeAttribute("managementErrorMessage");
        }

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

        //コメント投稿機能表示
        //セッションからエラーメッセージを取得して、コメントのエラー有無をチェック
        String errorMessage = (String)session.getAttribute("errorMessage");
        if(errorMessage != null){
            //エラーメッセージがあった場合はセッションからコメントフォームを取得
            CommentForm commentForm = (CommentForm)session.getAttribute("commentForm");
            //エラーになったtextをエラーtextに詰めなおす
            commentForm.setErrorText(commentForm.getText());
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

        //エラーメッセージ表示後のコメント登録対応
        //エラー後にテキストが入力された場合は、入力されたテキストをFormのtextに詰める
        if(commentForm.getErrorText() != null){
            text = commentForm.getErrorText();
            commentForm.setText(text);
        }

        //コメントのエラーチェック
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
    public ModelAndView updateUser(@PathVariable Integer id, @ModelAttribute("user") @Validated UserForm userForm, BindingResult result, @RequestParam(name="branch") Integer branchId, @RequestParam(name="department") Integer departmentId){
        ModelAndView mav = new ModelAndView();

        //userFormから値取得
        String password = userForm.getPassword();
        String passwordConfirmation = userForm.getPasswordConfirmation();


        //バリデーション必須チェック
        List<String> errorMessages = new ArrayList<String>();

        if (userForm.getAccount().isBlank()){
            errorMessages.add("・アカウントを入力してください");
        }
        if (userForm.getName().isBlank()){
            errorMessages.add("・氏名を入力してください");
        }
        if (Integer.toString(branchId).isBlank()){
            errorMessages.add("・支社を選択してください");
        }
        if (Integer.toString(departmentId).isBlank()){
            errorMessages.add("・部署を選択してください");
        }

        //パスワード・パスワード(確認用)が同じかチェック
        if(!password.equals(passwordConfirmation)) {
            errorMessages.add("・パスワードと確認用パスワードが一致しません");
        }

        //支社と部署の組み合わせが妥当か
        if((branchId == 1)  && (departmentId == 3 || departmentId == 4)) {
            errorMessages.add("・支社と部署の組み合わせが不正です");
        }
        if((branchId == 2 || branchId == 3 || branchId ==4)  && (departmentId == 1 || departmentId == 2)) {
            errorMessages.add("・支社と部署の組み合わせが不正です");
        }

        //重複チェック
        UserForm selectedAccount = userService.findByAccount(userForm.getAccount());
        if ((selectedAccount != null) && (selectedAccount.getId() != userForm.getId())) {
            errorMessages.add("・アカウントが重複しています");
        }

        //アカウント・パスワードの文字数チェック（アノテーションができなかった時用)
        if((!userForm.getAccount().isBlank()) && (!userForm.getAccount().matches("^[a-zA-Z0-9]{6,20}+$"))) {
            errorMessages.add("・アカウントは半角英数字かつ6文字以上20文字以下で入力してください");
        }

        if((!StringUtils.isBlank(password)) && (!password.matches("^[!-~]{6,20}+$"))) {
            errorMessages.add("・パスワードは半角文字かつ6文字以上20文字以下で入力してください");
        }

        if(result.hasErrors()) {
            //エラーがあったら、エラーメッセージを格納する
            //エラーメッセージの取得
            for (FieldError error : result.getFieldErrors()) {
                String message = error.getDefaultMessage();
                //取得したエラーメッセージをエラーメッセージのリストに格納
                errorMessages.add(message);
            }
        }

        if(!errorMessages.isEmpty()) {
            //エラーメッセージに値があれば、エラーメッセージを画面にバインド
            mav.addObject("errorMessages", errorMessages);
            //エラーメッセージ表示後も値保持するため、branchId,departmentIdをセットし直して画面にバインド
            userForm.setBranchId(branchId);
            userForm.setDepartmentId(departmentId);
            mav.addObject("user", userForm);
            //編集画面にフォワード処理
            mav.setViewName("/user_edit");
            return mav;
        }

        //idからユーザ情報参照
        UserForm editUserForm = userService.selectEditUser(id);

        //更新処理
        userForm.setId(id);
        userForm.setIsStopped(editUserForm.getIsStopped());
        userForm.setBranchId(branchId);
        userForm.setDepartmentId(departmentId);
        //パスワードの入力無いとき
        if(StringUtils.isBlank(password)){
            userForm.setPassword(editUserForm.getPassword());
            userService.saveUserPassword(userForm);
        } else {
            userService.saveUser(userForm);
        }

        //ユーザー管理画面へリダイレクト
        return new ModelAndView("redirect:/userManage");
    }

    /*
     *ユーザー登録画面表示
     */
    @GetMapping("/newEntry")
    public ModelAndView newEntry(){
        ModelAndView mav = new ModelAndView();

        //空のユーザー情報をセット
        UserForm newEntry = new UserForm();

        //画面にバインド
        mav.addObject("user", newEntry);

        //画面遷移先を指定
        mav.setViewName("/user_entry");

        //画面に遷移
        return mav;
    }

    /*
     *ユーザー登録処理
     */
    @PutMapping("/newEntry")
    public ModelAndView entryUser(@ModelAttribute("user") @Validated UserForm userForm, BindingResult result,
                                  @RequestParam(name="branch") Integer branchId,
                                  @RequestParam(name="department") Integer departmentId){
        ModelAndView mav = new ModelAndView();

        //バリデーション　必須チェック
        List<String> errorMessages = new ArrayList<String>();
        if (userForm.getAccount().isBlank()){
            errorMessages.add("・アカウントを入力してください");
        }
        if (userForm.getPassword().isBlank()){
            errorMessages.add("・パスワードを入力してください");
        }
        if (userForm.getName().isBlank()){
            errorMessages.add("・氏名を入力してください");
        }
        if (Integer.toString(branchId).isBlank()){
            errorMessages.add("・支社を選択してください");
        }
        if (Integer.toString(departmentId).isBlank()){
            errorMessages.add("・部署を選択してください");
        }
        //妥当性チェック　パスワードと確認用パスワードが同一か
        if (!userForm.getPassword().equals(userForm.getPasswordConfirmation())){
            errorMessages.add("・パスワードと確認用パスワードが一致しません");
        }
        //支社と部署の組み合わせが妥当か
        if((branchId == 1)  && (departmentId == 3 || departmentId == 4)) {
            errorMessages.add("・支社と部署の組み合わせが不正です");
        }
        if((branchId == 2 || branchId == 3 || branchId ==4)  && (departmentId == 1 || departmentId == 2)) {
            errorMessages.add("・支社と部署の組み合わせが不正です");
        }
        //重複チェック
        UserForm selectedAccount = userService.findByAccount(userForm.getAccount());
        if (selectedAccount != null){
            errorMessages.add("・アカウントが重複しています");
        }
        //アカウント・パスワードの文字数チェック（アノテーションができなかった時用)
        if((!userForm.getAccount().isBlank()) && !userForm.getAccount().matches("^[a-zA-Z0-9]{6,20}+$")) {
            errorMessages.add("・アカウントは半角英数字かつ6文字以上20文字以下で入力してください");
        }
        if((!userForm.getPassword().isBlank()) && !userForm.getPassword().matches("^[!-~]{6,20}+$")) {
            errorMessages.add("・パスワードは半角文字かつ6文字以上20文字以下で入力してください");
        }
        //氏名　文字数チェック
        if(result.hasErrors()) {
            //エラーがあったら、エラーメッセージを格納する
            //エラーメッセージの取得
            for (FieldError error : result.getFieldErrors()) {
                String message = error.getDefaultMessage();
                //取得したエラーメッセージをエラーメッセージのリストに格納
                errorMessages.add(message);
            }
        }
        //エラーメッセージを表示
        if (errorMessages.size() != 0){
            mav.addObject("errorMessages", errorMessages);

            //入力情報の保持
            userForm.setBranchId(branchId);
            userForm.setDepartmentId(departmentId);
            mav.addObject("user", userForm);
            mav.setViewName("/user_entry");
            return mav;
        }

        //登録処理
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