package com.vote.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vote.entity.R;
import com.vote.entity.VoteItem;
import com.vote.service.IVoteItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投票选项Controller控制器
 */
@RestController
@RequestMapping("/voteItem")
public class VoteItemController {

    @Autowired
    private IVoteItemService voteItemService;

    /**
     * 获取指定投票的投票选项排名
     * @param voteId
     * @return
     */
    @GetMapping("/rank/{voteId}")
    public R getRankByVoteId(@PathVariable(value = "voteId")Integer voteId){
        List<VoteItem> voteItemList = voteItemService.list(new QueryWrapper<VoteItem>().eq("vote_id", voteId).orderByDesc("number"));
        Map<String,Object> map=new HashMap<>();
        map.put("voteItemList",voteItemList);
        return R.ok(map);
    }
}
