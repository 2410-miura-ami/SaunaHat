package com.example.SaunaHat.service;

import com.example.SaunaHat.controller.form.CommentForm;
import com.example.SaunaHat.controller.form.MessageForm;
import com.example.SaunaHat.repository.CommentRepository;
import com.example.SaunaHat.repository.entity.Comment;
import com.example.SaunaHat.repository.entity.Message;
import com.example.SaunaHat.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    /*
     * コメント表示処理
     */
    public List<CommentForm> findAllComment() {
        List<Comment> results = commentRepository.findAllComment();
        //EntityからFormに詰め替え
        List<CommentForm> comments = setCommentForm(results);
        return comments;
    }

    /*
     * DBから取得したデータをFormに設定
     */
    private List<CommentForm> setCommentForm(List<Comment> results) {
        List<CommentForm> comments = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            CommentForm comment = new CommentForm();
            Comment result = results.get(i);

            comment.setId(result.getId());
            comment.setText(result.getText());
            comment.setMessageId(result.getMessageId());
            comment.setUserName(result.getUser().getName());
            comment.setUserAccount(result.getUser().getAccount());

            comments.add(comment);
        }
        return comments;

    }

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
     * コメント削除処理
     */
    public void deleteComment(Integer id) {
        commentRepository.deleteById(id);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Comment setCommentEntity(CommentForm commentForm) {
        Comment comment = new Comment();

        //ユーザIDをUser型にする
        User user = new User();
        user.setId(commentForm.getUserId());

        //投稿IDとテキストとUser型のユーザIDをComment型のEntityに詰める
        comment.setMessageId(commentForm.getMessageId());
        comment.setUser(user);
        comment.setText(commentForm.getText());
        return comment;
    }
}
