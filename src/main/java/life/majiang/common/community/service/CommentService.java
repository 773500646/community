package life.majiang.common.community.service;

import life.majiang.common.community.dto.CommentDTO;
import life.majiang.common.community.enums.CommentTypeEnum;
import life.majiang.common.community.enums.NotificationTypeEnum;
import life.majiang.common.community.enums.NotificationStatusEnum;
import life.majiang.common.community.exception.CustomizeErrorCode;
import life.majiang.common.community.exception.CustomizeException;
import life.majiang.common.community.mapper.*;
import life.majiang.common.community.model.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private Question1Mapper question1Mapper;

    @Autowired
    private Question1ExtMapper question1ExtMapper;

    @Autowired
    private User1Mapper user1Mapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, User1 commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_NOT_WRONG);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            // 回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }

            // 回复问题
            Question1 question1 = question1Mapper.selectByPrimaryKey(dbComment.getParentId());
            if (question1 == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            commentMapper.insert(comment);

            // 增加评论数
            Comment parentComment = new Comment();
            parentComment.setCommentCount(1);
            parentComment.setId(comment.getParentId());
            commentExtMapper.incCommentCount(parentComment);

            createNotify(comment, dbComment.getCommentator(),commentator.getName(), question1.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question1.getId());
        } else {
            // 回复问题
            Question1 question1 = question1Mapper.selectByPrimaryKey(comment.getParentId());
            if (question1 == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            comment.setCommentCount(0);
            commentMapper.insert(comment);
            question1.setCommentCount(1);
            question1ExtMapper.incCommentCount(question1);
            // 创建通知
            createNotify(comment, question1.getCreator(),commentator.getName(),question1.getTitle(), NotificationTypeEnum.REPLY_QUESTION, question1.getId());
        }
    }

    private void createNotify(Comment comment, Integer receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationTypeEnum, Integer outerId) {
        if (receiver.equals(comment.getCommentator())) {
            return ;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationTypeEnum.getType());
        notification.setOuterId(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByTargetId(Integer id, CommentTypeEnum type) {
        CommentExample example = new CommentExample();
        example.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        List<Comment> comments = commentMapper.selectByExample(example);

        if(comments.size() == 0) {
            return new ArrayList<>();
        }
        // 获取去重的评论人
        Set<Integer> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Integer> users = new ArrayList<>();
        users.addAll(commentators);

        // 获取评论人并转换为Map
        User1Example user1Example = new User1Example();
        user1Example.createCriteria()
                .andAccountIdIn(users);
        List<User1> user1s = user1Mapper.selectByExample(user1Example);
        Map<Integer, User1> user1Map = user1s.stream().collect(Collectors.toMap(user -> user.getAccountId(), user -> user));

        // 转换 comment 为commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(user1Map.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        //user1Mapper.sel
        return commentDTOS;
    }
}
