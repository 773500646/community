package life.majiang.common.community.dto;

import life.majiang.common.community.model.User1;
import lombok.Data;

@Data
public class NotificationDTO {

    private Integer id;
    private Long gmtCreate;
    private Integer status;
    private User1 user;
    private String outerTitle;
    private String typeName;
    private Integer outerId;
    private String notifierName;
    private Integer type;
}
