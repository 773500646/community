package life.majiang.common.community.dto;

import life.majiang.common.community.model.User;
import life.majiang.common.community.model.User1;
import lombok.Data;

@Data
public class QuestionDTO {
    private Integer id;
    private String title;
    private String description;
    private String tag;
    private Long gmt_create;
    private Long gmt_modified;
    private Integer creator;
    private Integer view_count;
    private Integer comment_count;
    private Integer likeCount;
    //private User user;
    private User1 user;
}
