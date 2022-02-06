package com.nowcoder.community.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class Comment {
    private Long id;
    private Long userId;
    private Integer entityType;
    private Long entityId;
    private Long targetId;
    private String content;
    private Integer status;
    private Date createTime;
}
