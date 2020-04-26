package life.majiang.common.community.service;

import life.majiang.common.community.dto.NotificationDTO;
import life.majiang.common.community.dto.PaginationDTO;
import life.majiang.common.community.enums.NotificationStatusEnum;
import life.majiang.common.community.enums.NotificationTypeEnum;
import life.majiang.common.community.exception.CustomizeErrorCode;
import life.majiang.common.community.exception.CustomizeException;
import life.majiang.common.community.mapper.NotificationMapper;
import life.majiang.common.community.mapper.User1Mapper;
import life.majiang.common.community.model.Notification;
import life.majiang.common.community.model.NotificationExample;
import life.majiang.common.community.model.User1;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private User1Mapper user1Mapper;

    public PaginationDTO<NotificationDTO> list(Integer account_id, Integer page, Integer size) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();

        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(account_id)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        Integer totalCount = (int)notificationMapper.countByExample(notificationExample);
        System.out.println(totalCount);
        paginationDTO.setPagination(totalCount, page, size);
        if (page < 1) {
            page = 1;
        }

        if (page > paginationDTO.getTotalPage()) {
            page = paginationDTO.getTotalPage();
        }

        Integer offset = size * (page -1);
        //List<Question> questionList = questionMapper.listByUserId(account_id, offset, size);

        NotificationExample notificationExample1 = new NotificationExample();
        notificationExample1.setOrderByClause("`gmt_create` DESC");
        notificationExample1.createCriteria()
                .andReceiverEqualTo(account_id)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        List<Notification> notificationList = notificationMapper.selectByExampleWithRowbounds(notificationExample1, new RowBounds(offset, size));
        if (notificationList.size() == 0) {
            return paginationDTO;
        }

//        Set<Integer> integerSet = notificationList.stream().map(notify -> notify.getNotifier()).collect(Collectors.toSet());
//        ArrayList<Integer> integers = new ArrayList<>(integerSet);
//        User1Example example = new User1Example();
//        example.createCriteria().andAccountIdIn(integers);
//        List<User1> user1s = user1Mapper.selectByExample(example);

        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification: notificationList) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            // notificationDTO.setId(notification.getOuterId());
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }

        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public Integer unreadCount(Integer id) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(id)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        Integer count = (int)notificationMapper.countByExample(notificationExample);
        return count;
    }

    public NotificationDTO read(Integer id, User1 user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if(!Objects.equals(notification.getReceiver(), user.getAccountId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FALT);
        }

        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;
    }
}
