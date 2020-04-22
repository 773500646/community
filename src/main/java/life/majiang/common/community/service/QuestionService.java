package life.majiang.common.community.service;

import life.majiang.common.community.dto.PaginationDTO;
import life.majiang.common.community.dto.QuestionDTO;
import life.majiang.common.community.mapper.Question1Mapper;
import life.majiang.common.community.mapper.QuestionMapper;
import life.majiang.common.community.mapper.User1Mapper;
import life.majiang.common.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

//    @Autowired
//    private UserMapper userMapper;

    @Autowired
    private User1Mapper user1Mapper;

    @Autowired
    private Question1Mapper question1Mapper;

    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        //Integer totalCount = questionMapper.count();
        Integer totalCount = (int)question1Mapper.countByExample(new Question1Example());
        paginationDTO.setPagination(totalCount, page, size);
        if (page < 1) {
            page = 1;
        }

        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        Integer offset = size * (page -1);
        //List<Question> questionList = questionMapper.list(offset, size);
        List<Question1> question1s = question1Mapper.selectByExampleWithRowbounds(new Question1Example(), new RowBounds(offset, size));
        List<QuestionDTO> questionDTOS = new ArrayList<>();


        for (Question1 question: question1s) {
            //User user = userMapper.findByAccountId(question.getCreator());
            User1Example user1Example = new User1Example();
            user1Example.createCriteria()
                    .andAccountIdEqualTo(question.getCreator());
            List<User1> user1 = user1Mapper.selectByExample(user1Example);
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user1.get(0));
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOS);

        return paginationDTO;
    }

    public PaginationDTO list(Integer account_id, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        //Integer totalCount = questionMapper.countByUserId(account_id);

        Question1Example question1Example = new Question1Example();
        question1Example.createCriteria()
                .andCreatorEqualTo(account_id);
        Integer totalCount = (int)question1Mapper.countByExample(question1Example);

        paginationDTO.setPagination(totalCount, page, size);
        if (page < 1) {
            page = 1;
        }

        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        Integer offset = size * (page -1);
        //List<Question> questionList = questionMapper.listByUserId(account_id, offset, size);

        Question1Example question1Example1 = new Question1Example();
        question1Example1.createCriteria()
                .andCreatorEqualTo(account_id);
        List<Question1> question1s = question1Mapper.selectByExampleWithRowbounds(question1Example1, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOS = new ArrayList<>();


        for (Question1 question: question1s) {
            // User user = userMapper.findByAccountId(question.getCreator());
            User1Example user1Example = new User1Example();
            user1Example.createCriteria()
                    .andAccountIdEqualTo(question.getCreator());
            List<User1> user1 = user1Mapper.selectByExample(user1Example);
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user1.get(0));
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOS);

        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question1 question = question1Mapper.selectByPrimaryKey(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        //User user = userMapper.findByAccountId(question.getCreator());
        User1Example user1Example = new User1Example();
        user1Example.createCriteria()
                .andAccountIdEqualTo(question.getCreator());
        List<User1> user1 = user1Mapper.selectByExample(user1Example);
        questionDTO.setUser(user1.get(0));
        return questionDTO;
    }

    public void createOrUpdate(Question1 question) {
        if (question.getId() == null) {
            // 创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question1Mapper.insert(question);
        } else {
            // 更新
            // question.setGmtModified(System.currentTimeMillis());
            // Question1 question1 = new Question1();
            question.setGmtModified(System.currentTimeMillis());
            Question1Example example = new Question1Example();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            question1Mapper.updateByExampleSelective(question, example);
        }
    }
}
