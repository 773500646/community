package life.majiang.common.community.mapper;

import life.majiang.common.community.model.Question1;
import life.majiang.common.community.model.Question1Example;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface Question1ExtMapper {
    int incView(Question1 record);
    int incCommentCount(Question1 record);
}