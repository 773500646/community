package life.majiang.common.community.service;

import life.majiang.common.community.dto.PaginationDTO;
import life.majiang.common.community.dto.QuestionDTO;
import life.majiang.common.community.mapper.QuestionMapper;
import life.majiang.common.community.mapper.User1Mapper;
import life.majiang.common.community.mapper.UserMapper;
import life.majiang.common.community.model.Question;
import life.majiang.common.community.model.User;
import life.majiang.common.community.model.User1;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private User1Mapper user1Mapper;

    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = questionMapper.count();
        paginationDTO.setPagination(totalCount, page, size);
        if (page < 1) {
            page = 1;
        }

        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        Integer offset = size * (page -1);
        List<Question> questionList = questionMapper.list(offset, size);
        List<QuestionDTO> questionDTOS = new ArrayList<>();


        for (Question question: questionList) {
            //User user = userMapper.findByAccountId(question.getCreator());
            User1 user1 = user1Mapper.selectByPrimaryAccountId(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user1);
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOS);

        return paginationDTO;
    }

    public PaginationDTO list(Integer account_id, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = questionMapper.countByUserId(account_id);
        paginationDTO.setPagination(totalCount, page, size);
        if (page < 1) {
            page = 1;
        }

        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        Integer offset = size * (page -1);
        List<Question> questionList = questionMapper.listByUserId(account_id, offset, size);
        List<QuestionDTO> questionDTOS = new ArrayList<>();


        for (Question question: questionList) {
            // User user = userMapper.findByAccountId(question.getCreator());
            User1 user1 = user1Mapper.selectByPrimaryAccountId(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user1);
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOS);

        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.getById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        //User user = userMapper.findByAccountId(question.getCreator());
        User1 user1 = user1Mapper.selectByPrimaryAccountId(question.getCreator());
        questionDTO.setUser(user1);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            // 创建
            question.setGmt_create(System.currentTimeMillis());
            question.setGmt_modified(question.getGmt_create());
            questionMapper.create(question);
        } else {
            // 更新
            question.setGmt_modified(System.currentTimeMillis());
            questionMapper.update(question);
        }
    }
}
