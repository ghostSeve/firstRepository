package com.vote.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.vote.entity.R;
import com.vote.entity.VoteDetail;
import com.vote.entity.VoteItem;
import com.vote.entity.WxUserInfo;
import com.vote.service.IVoteDetailService;
import com.vote.service.IVoteItemService;
import com.vote.service.IWxUserInfoService;
import com.vote.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 投票详情Controller控制器
 */
@RestController
@RequestMapping("/voteDetail")
public class VoteDetailController {

    @Autowired
    private IVoteDetailService voteDetailService;

    @Autowired
    private IVoteItemService voteItemService;

    @Autowired
    private IWxUserInfoService wxUserInfoService;

    /**
     * 添加投票
     * @param voteDetail
     * @param token
     * @return
     */
    @RequestMapping("/add")
    @Transactional
    public R add(@RequestBody VoteDetail voteDetail, @RequestHeader String token){
        Claims claims = JwtUtils.validateJWT(token).getClaims();
        String openid=claims.getId();
        HashMap<String,Object> resultMap=new HashMap<>();
        // 一个投票用户只能投票一次
        int count = voteDetailService.count(new QueryWrapper<VoteDetail>().eq("openid", openid).eq("vote_id", voteDetail.getVoteId()));
        if(count>0){
            resultMap.put("info","您已经投票过，不能重复投票！");
        }else{
            // 更新投票项
            voteItemService.update(new UpdateWrapper<VoteItem>().setSql("number=number+1").eq("id",voteDetail.getVoteItemId()));
            voteDetail.setOpenid(openid);
            voteDetail.setVoteDate(new Date());
            voteDetailService.save(voteDetail);
            resultMap.put("info","投票成功！");
        }
        return R.ok(resultMap);

    }

    /**
     * 根据id查询投票人详情
     * @param voteId
     * @param voteItemId
     * @return
     */
    @GetMapping("/{voteId}/{voteItemId}")
    public R findById(@PathVariable(value = "voteId")Integer voteId,@PathVariable(value = "voteItemId")Integer voteItemId){
        List<VoteDetail> voteDetailList = voteDetailService.list(new QueryWrapper<VoteDetail>().eq("vote_id", voteId).eq("vote_item_id", voteItemId));
        for(VoteDetail voteDetail:voteDetailList){
            WxUserInfo wxUserInfo = wxUserInfoService.getOne(new QueryWrapper<WxUserInfo>().eq("openid", voteDetail.getOpenid()));
            voteDetail.setWxUserInfo(wxUserInfo);
        }
        HashMap<String,Object> resultMap=new HashMap<>();
        resultMap.put("voteDetailList",voteDetailList);
        return R.ok(resultMap);
    }
}
