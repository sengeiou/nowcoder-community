package com.nowcoder.community.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class LoginTicket {
    private Long id;
    private Long userId;
    private String ticket;
    private Integer status;
    private Date expired;
}
