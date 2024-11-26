package com.example.SaunaHat.filter;


import com.example.SaunaHat.controller.form.CommentForm;
import com.example.SaunaHat.controller.form.UserForm;
import com.example.SaunaHat.repository.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class ManagementFilter implements Filter {

    /*initは必須じゃないようなのでコメントアウト
    @Override
    public void init(FilterConfig config) {
    }*/

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //セッション取得準備
        HttpSession session = ((HttpServletRequest) request).getSession();
        //セッションからログインユーザ情報を取得
        UserForm userForm = (UserForm)session.getAttribute("loginUser");
        //ログインユーザ情報から部署IDを取得
        int departmentId = userForm.getDepartmentId();
        //部署IDが人事総務部のID（１）と合致するかチェック
        if(departmentId == 1){
            chain.doFilter(request, response);
        }else{
            //部署IDが合致しない場合、エラーメッセージをセッションにセット
            String managementErrorMessage = "無効なアクセスです";
            session.setAttribute("managementErrorMessage", managementErrorMessage);
            //ホーム画面にリダイレクト
            ((HttpServletResponse) response).sendRedirect("/home");
        }
    }

    @Override
    public void destroy() {
    }
}

