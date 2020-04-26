package life.majiang.common.community.controller;

import life.majiang.common.community.dto.NotificationDTO;
import life.majiang.common.community.dto.PaginationDTO;
import life.majiang.common.community.dto.QuestionDTO;
import life.majiang.common.community.mapper.UserMapper;
import life.majiang.common.community.model.User1;
import life.majiang.common.community.service.NotificationService;
import life.majiang.common.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size) {

        //User user = (User)request.getSession().getAttribute("user");;
        User1 user = (User1) request.getSession().getAttribute("user");;
        if (user == null) {
            return "redirect:/";
        }

        if ("questions".equals(action)) {
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            PaginationDTO<QuestionDTO> paginationDTO = questionService.list(user.getAccountId(), page, size);
            model.addAttribute("pagination", paginationDTO);
        } else if ("replies".equals(action)) {
            PaginationDTO<NotificationDTO> paginationDTO = notificationService.list(user.getAccountId(), page, size);
            Integer unreadCount = notificationService.unreadCount(user.getAccountId());
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "我的回复");
            model.addAttribute("pagination", paginationDTO);
            model.addAttribute("unreadCount", unreadCount);
        }

        return "profile";
    }
}
