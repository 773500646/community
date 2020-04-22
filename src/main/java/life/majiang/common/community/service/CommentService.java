package life.majiang.common.community.service;

import life.majiang.common.community.enums.CommentTypeEnum;
import life.majiang.common.community.exception.CustomizeErrorCode;
import life.majiang.common.community.exception.CustomizeException;
import life.majiang.common.community.mapper.CommentMapper;
import life.majiang.common.community.mapper.Question1ExtMapper;
import life.majiang.common.community.mapper.Question1Mapper;
import life.majiang.common.community.model.Comment;

import life.majiang.common.community.model.Question1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private Question1Mapper question1Mapper;

    @Autowired
    private Question1ExtMapper question1ExtMapper;

    public void insert(Comment comment) {
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
            commentMapper.insert(comment);
        } else {
            // 回复问题
            Question1 question1 = question1Mapper.selectByPrimaryKey(comment.getParentId());
            if (question1 == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question1.setCommentCount(1);
            question1ExtMapper.incCommentCount(question1);
        }
    }
}
