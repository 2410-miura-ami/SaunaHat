package com.example.SaunaHat.filter;

import com.example.SaunaHat.controller.form.UserForm;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //セッション取得準備
        HttpSession session = ((HttpServletRequest) request).getSession();
        //セッションからログインユーザ情報を取得
        UserForm userForm = (UserForm)session.getAttribute("loginUser");
        //ログインユーザ情報が空かチェック
        if(userForm != null){
            //ログインユーザ情報が空じゃない場合は、処理を続行
            chain.doFilter(request, response);
        }else{
            //ログインユーザ情報が空の場合は、エラーメッセージをセッションにセット
            List<String> errorMessages = new ArrayList<String>();
            errorMessages.add("ログインしてください");
            session.setAttribute("errorMessages", errorMessages);
            //ログイン画面にリダイレクト
            ((HttpServletResponse) response).sendRedirect("/");
        }
    }

    @Override
    public void destroy() {
    }

}
