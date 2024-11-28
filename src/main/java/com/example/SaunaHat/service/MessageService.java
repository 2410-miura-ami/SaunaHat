package com.example.SaunaHat.service;

import com.example.SaunaHat.controller.form.MessageForm;
import com.example.SaunaHat.controller.form.UserForm;
import com.example.SaunaHat.repository.MessageRepository;
import com.example.SaunaHat.repository.UserRepository;
import com.example.SaunaHat.repository.entity.Message;
import com.example.SaunaHat.repository.entity.User;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;
    /*@Autowired
    private ReportMapper reportMapper;*/

    /*
     * レコード全件取得処理
     */
    public List<MessageForm> findAllMessage(String startDate, String endDate, String category) {
        //絞り込み開始日の入力チェック
        if(StringUtils.isBlank(startDate)){
            //開始日の入力がない場合はデフォルト値をセット
            startDate = "2022-01-01 00:00:00";
        }else{
            //開始日の入力があった場合は時間部分をセット
            startDate = startDate + " 00:00:00";
        }
        //絞り込み終了日の入力チェック
        if(StringUtils.isBlank(endDate)){
            //終了日の入力がない場合は現在日時をセット
            Date date = new Date();
            endDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            endDate = endDate + " 23:59:59";
        }else{
            //終了日の入力があった場合は時間部分をセット
            endDate = endDate + " 23:59:59";
        }
        //startDateとendDateをDate型に変換
        Date StartDate = null;
        Date EndDate = null;
        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StartDate = sdFormat.parse(startDate);
            EndDate = sdFormat.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //空のEntityリストを用意
        List<Message> results;
        //カテゴリの絞り込み入力チェック
        if(StringUtils.isBlank(category)){
            //カテゴリの絞り込みなしでセレクト
            results = messageRepository.selectMessage(StartDate, EndDate);
        }else{
            //カテゴリの絞り込みありでセレクト
            results = messageRepository.selectMessageByCategory(StartDate, EndDate, "%" + category + "%");
        }
        //EntityからFormに詰め替え
        List<MessageForm> reports = setMessageForm(results);
        return reports;

    }

    /*
     * DBから取得したデータをFormに設定
     */
    private List<MessageForm> setMessageForm(List<Message> results) {
        List<MessageForm> messages = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            MessageForm message = new MessageForm();
            Message result = results.get(i);
            message.setId(result.getId());
            message.setTitle(result.getTitle());
            message.setText(result.getText());
            message.setCategory(result.getCategory());
            message.setCreatedDate(result.getCreatedDate());
            message.setUserId(result.getUser().getId());
            message.setUserAccount(result.getUser().getAccount());
            message.setUserName(result.getUser().getName());

            messages.add(message);
        }
        return messages;
    }

    /*
     *新規投稿登録
     */
    public void saveMessage(MessageForm reqMessage, UserForm reqLoginUser) {
        //ログインユーザ情報をUser型で取得
        String loginAccount = reqLoginUser.getAccount();
        List<User> results = userRepository.selectUser(loginAccount);
        User loginUser = results.get(0);
        //投稿、ログインユーザ情報をEntityに詰める
        Message saveMessage = setMessageEntity(reqMessage, loginUser);
        //投稿をDBに登録
        messageRepository.save(saveMessage);
    }
    /*
     *投稿をEntityに詰める
     */
    private Message setMessageEntity(MessageForm reqMessage, User loginUser){
        Message message = new Message();
        message.setTitle(reqMessage.getTitle());
        message.setText(reqMessage.getText());
        message.setCategory(reqMessage.getCategory());
        message.setUser(loginUser);
        return message;
    }

    /*
     * レコード削除
     */
    public void deleteMessage(Integer id) {
        messageRepository.deleteById(id);
    }
}

