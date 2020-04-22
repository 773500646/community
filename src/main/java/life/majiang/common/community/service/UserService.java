package life.majiang.common.community.service;

import life.majiang.common.community.mapper.User1Mapper;
import life.majiang.common.community.mapper.UserMapper;
import life.majiang.common.community.model.User;
import life.majiang.common.community.model.User1;
import life.majiang.common.community.model.User1Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private User1Mapper user1Mapper;

    public void createOrUpdate(User user) {
        //User dbUser = userMapper.findByAccountId(user.getAccount_id());

        User1Example user1Example = new User1Example();
        user1Example.createCriteria()
                .andAccountIdEqualTo(user.getAccount_id());
        List<User1> user1s = user1Mapper.selectByExample(user1Example);
        if(user1s.size() == 0) {
            // 插入
            user.setGmt_create(System.currentTimeMillis());
            user.setGmt_modified(user.getGmt_create());
            userMapper.insert(user);
        } else {
            // 更新
            User1 dbUser = user1s.get(0);
            User1 updateUser = new User1();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatar_url());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            User1Example user1Example1 = new User1Example();
            user1Example1.createCriteria()
                    .andIdEqualTo(dbUser.getId());
            user1Mapper.updateByExampleSelective(updateUser, user1Example1);
            //userMapper.update(dbUser);
        }
    }
}
