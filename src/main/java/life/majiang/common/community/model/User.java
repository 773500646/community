package life.majiang.common.community.model;

import lombok.Data;

@Data
public class User {
    private Integer id;
    private String name;
    private Integer account_id;
    private String token;
    private Long gmt_create;
    private Long gmt_modified;
    private String avatar_url;
}
