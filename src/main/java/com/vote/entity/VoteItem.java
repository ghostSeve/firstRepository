package com.vote.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 投票选项实体id:
 */
@TableName("t_vote_item")
@Data
public class VoteItem {

    private Integer id; // 编号

    private Integer voteId; // 投票ID

    private String name; // 投票选项名称

    private String image; // 投票选项图片

    private Integer number; // 票数

}
