package life.majiang.common.community.controller;

import life.majiang.common.community.cache.TagCache;
import life.majiang.common.community.dto.QuestionDTO;
import life.majiang.common.community.mapper.QuestionMapper;
import life.majiang.common.community.model.Question;
import life.majiang.common.community.model.Question1;
import life.majiang.common.community.model.User;
import life.majiang.common.community.model.User1;
import life.majiang.common.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish/{id}")
    private String edit (@PathVariable(name = "id") Integer id,
                         Model model) {
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(
            Model model
    ) {
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "id", required = false) Integer id,
            HttpServletRequest request,
            Model model
    ) {
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag",tag);

        if(title == null || title == ""){
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if(description == null || description == ""){
            model.addAttribute("error", "补充不能为空");
            return "publish";
        }
        if(tag == null || tag == ""){
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }

        User1 user = (User1)request.getSession().getAttribute("user");;
        if(user == null) {
            model.addAttribute("error", "用户未登录");
            return "publish";
        }

        String s = TagCache.filterInvalid(tag);
        if (StringUtils.isNotBlank(s)) {
            model.addAttribute("error", "输入非法标签" + s);
            return "publish";
        }

        //Question question = new Question();
        Question1 question = new Question1();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getAccountId());
        question.setId(id);

        questionService.createOrUpdate(question);
        return "redirect:/";
    }
}
