package com.nowcoder.community.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class Message {
    private Long id;
    private Long fromId;
    private Long toId;
    private String conversationId;
    private String content;
    private Integer status;
    private Date createTime;
}
