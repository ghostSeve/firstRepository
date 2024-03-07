package com.vote.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.Date;

/**投票详情
 */
@TableName("t_vote_detail")
@Data
public class VoteDetail {

    private Integer id; // 编号

    private Integer voteId; // 投票ID

    private Integer voteItemId; // 投票选项ID

    private String openid; // 投票人openid

    @TableField(select=false,exist = false)
    private WxUserInfo wxUserInfo;

    @JsonSerialize(using=CustomDateTimeSerializer.class)
    private Date voteDate;  // 投票时间

}
