package life.majiang.common.community.mapper;

import life.majiang.common.community.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modified,avatar_url) values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    public void insert(User user);

    @Select("select * from user where token = #{token} limit 1")
    public User findByToken(@Param("token") String token);

    @Select("select * from user where account_id = #{creator} order by id desc limit 1")
    public User findByAccountId(@Param("creator") Integer creator);

    @Update("update user set name = #{name}, token = #{token}, gmt_modified = #{gmt_modified}, avatar_url = #{avatar_url} where id = #{id}")
    void update(User dbUser);
}
