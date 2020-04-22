package life.majiang.common.community.mapper;

import life.majiang.common.community.dto.QuestionDTO;
import life.majiang.common.community.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionMapper {
    @Insert("insert into question (title,description,gmt_create,gmt_modified,creator,tag) values(#{title},#{description},${gmt_create},#{gmt_modified},#{creator},#{tag})")
    public void create(Question question);

    @Select("select * from question limit #{offset}, #{size}")
    List<Question> list(@Param("offset") Integer offset,@Param("size") Integer size);

    @Select("select count(1) from question")
    Integer count();

    @Select("select * from question where creator = #{account_id} limit #{offset}, #{size}")
    List<Question> listByUserId(@Param("account_id")Integer account_id,@Param("offset") Integer offset,@Param("size") Integer size);

    @Select("select count(1) from question where creator = #{account_id}")
    Integer countByUserId(@Param("account_id")Integer account_id);

    @Select("select * from question where id = #{id}")
    Question getById(@Param("id") Integer id);

    @Update("update question set title = #{title}, description = #{description}, gmt_modified = #{gmt_modified}, tag = #{tag} where id = #{id}")
    void update(Question question);
}
