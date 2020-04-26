package life.majiang.common.community.service;

import life.majiang.common.community.dto.PaginationDTO;
import life.majiang.common.community.dto.QuestionDTO;
import life.majiang.common.community.dto.QuestionQueryDTO;
import life.majiang.common.community.exception.CustomizeErrorCode;
import life.majiang.common.community.exception.CustomizeException;
import life.majiang.common.community.mapper.Question1ExtMapper;
import life.majiang.common.community.mapper.Question1Mapper;
import life.majiang.common.community.mapper.QuestionMapper;
import life.majiang.common.community.mapper.User1Mapper;
import life.majiang.common.community.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

//    @Autowired
//    private UserMapper userMapper;

    @Autowired
    private User1Mapper user1Mapper;

    @Autowired
    private Question1ExtMapper question1ExtMapper;

    @Autowired
    private Question1Mapper question1Mapper;

    public PaginationDTO<QuestionDTO> list(String search, Integer page, Integer size) {

        if (StringUtils.isNotBlank(search) ) {
            String[] split = StringUtils.split(search, " ");
            search = Arrays.stream(split).collect(Collectors.joining("|"));
        } else {
            search = null;
        }
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        //Integer totalCount = questionMapper.count();
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        Integer totalCount = question1ExtMapper.countBySearch(questionQueryDTO);
        paginationDTO.setPagination(totalCount, page, size);
        if (page < 1) {
            page = 1;
        }

        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        Integer offset = size * (page -1);
        //List<Question> questionList = questionMapper.list(offset, size);
        Question1Example example = new Question1Example();
        example.setOrderByClause("`gmt_create` DESC");
        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
        List<Question1> question1s = question1ExtMapper.selectBySearch(questionQueryDTO);

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
        paginationDTO.setData(questionDTOS);

        return paginationDTO;
    }

    public PaginationDTO<QuestionDTO> list(Integer account_id, Integer page, Integer size) {
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
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
        question1Example1.setOrderByClause("`gmt_create` DESC");
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
        paginationDTO.setData(questionDTOS);

        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question1 question = question1Mapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
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
            // 创建s
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            question1Mapper.insert(question);
        } else {
            // 更新
            // question.setGmtModified(System.currentTimeMillis());
            // Question1 question1 = new Question1();
            question.setGmtModified(System.currentTimeMillis());
            Question1Example example = new Question1Example();
            example.createCriteria()
                    .andIdEqualTo(question.getId());
            int updated = question1Mapper.updateByExampleSelective(question, example);
            if (updated != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Integer id) {
        Question1 record = new Question1();
        record.setId(id);
        question1ExtMapper.incView(record);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO questionDTO) {
        if (StringUtils.isBlank(questionDTO.getTag()) ) {
            return new ArrayList<>();
        }
        String[] split = StringUtils.split(questionDTO.getTag(), ",");
        String collect = Arrays.stream(split).collect(Collectors.joining("|"));
        Question1 question = new Question1();
        question.setId(questionDTO.getId());
        question.setTag(collect);

        List<Question1> question1List = question1ExtMapper.selectRelated(question);
        List<QuestionDTO> coll = question1List.stream().map(q -> {
            QuestionDTO questionDTO1 = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO1);
            return questionDTO1;
        }).collect(Collectors.toList());

        return coll;
    }
}
