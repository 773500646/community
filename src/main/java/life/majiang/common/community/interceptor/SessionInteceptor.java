package life.majiang.common.community.interceptor;

import life.majiang.common.community.mapper.User1Mapper;
import life.majiang.common.community.mapper.UserMapper;
import life.majiang.common.community.model.User;
import life.majiang.common.community.model.User1;
import life.majiang.common.community.model.User1Example;
import life.majiang.common.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class SessionInteceptor implements HandlerInterceptor {

    @Autowired
    private User1Mapper user1Mapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length != 0) {
            for (Cookie cookie: cookies) {
                if(cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    User1Example user1Example = new User1Example();
                    user1Example.createCriteria()
                            .andTokenEqualTo(token);
                    List<User1> users = user1Mapper.selectByExample(user1Example);
                    if(users.size() != 0) {
                        request.getSession().setAttribute("user", users.get(0));
                        Integer unreadCount = notificationService.unreadCount(users.get(0).getAccountId());
                        request.getSession().setAttribute("unreadNotification", unreadCount);
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
