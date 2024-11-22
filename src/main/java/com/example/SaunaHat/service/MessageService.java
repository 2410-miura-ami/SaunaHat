package com.example.SaunaHat.service;

import com.example.SaunaHat.controller.form.MessageForm;
import com.example.SaunaHat.repository.MessageRepository;
import com.example.SaunaHat.repository.entity.Message;
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
    /*@Autowired
    private ReportMapper reportMapper;*/

    /*
     * レコード全件取得処理
     */
    public List<MessageForm> findAllMessage(String startDate, String endDate, String category) {
        //日時の絞り込みの入力チェック
        if(startDate.isBlank()){
            //開始日の入力がない場合はデフォルト値をセット
            startDate = "2022-01-01 00:00:00";
        }else{
            //開始日の入力があった場合は時間部分をセット
            startDate = startDate + "00:00:00";
        }
        //カテゴリの絞り込み入力チェック
        if(category.isBlank()){
            
        }


        //repositoryを呼び出して、戻り値をEntityとして受け取る
        List<Message> results = messageRepository.selectMessage();
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
            message.setUserAccount(result.getUser().getAccount());
            message.setUserName(result.getUser().getName());

            messages.add(message);
        }
        return messages;
    }


}

