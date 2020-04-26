package life.majiang.common.community.mapper;

import life.majiang.common.community.model.Comment;
import life.majiang.common.community.model.CommentExample;
import life.majiang.common.community.model.Question1;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface CommentExtMapper {
    int incCommentCount(Comment comment);
}