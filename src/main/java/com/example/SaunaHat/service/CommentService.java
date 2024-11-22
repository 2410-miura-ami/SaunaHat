package com.example.SaunaHat.service;

import com.example.SaunaHat.controller.form.CommentForm;
import com.example.SaunaHat.controller.form.MessageForm;
import com.example.SaunaHat.repository.CommentRepository;
import com.example.SaunaHat.repository.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    /*
     * コメント表示処理
     *
    public List<CommentForm> findAllComment(){
        List<Comment> results = commentRepository.findAllComment();
        //EntityからFormに詰め替え
        List<CommentForm> comments = setCommentForm(results);
        return reports;
    }*/

    /*
     * コメント登録処理
     */
    public void addComment(CommentForm commentForm) {
        //FormからEntityに詰め替え
        Comment comment = setCommentEntity(commentForm);
        //Entityを引数にDBに追加
        commentRepository.save(comment);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Comment setCommentEntity(CommentForm commentForm) {
        Comment comment = new Comment();
        comment.setMessageId(commentForm.getMessageId());
        comment.setUserId(commentForm.getUserId());
        comment.setText(commentForm.getText());
        return comment;
    }
}
