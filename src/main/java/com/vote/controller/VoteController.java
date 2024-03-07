package com.vote.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vote.entity.*;
import com.vote.service.IVoteDetailService;
import com.vote.service.IVoteItemService;
import com.vote.service.IVoteService;
import com.vote.service.IWxUserInfoService;
import com.vote.util.DateUtil;
import com.vote.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投票Controller控制器
 */
@RestController
@RequestMapping("/vote")
public class VoteController {

    @Value("${coverImagesFilePath}")
    private String coverImagesFilePath;

    @Value("${voteItemImagesFilePath}")
    private String voteItemImagesFilePath;

    @Autowired
    private IVoteService voteService;

    @Autowired
    private IVoteItemService voteItemService;

    @Autowired
    private IWxUserInfoService wxUserInfoService;

    @Autowired
    private IVoteDetailService voteDetailService;

    /**
     * 上传封面图片
     * @param coverImage
     * @return
     * @throws Exception
     */
    @RequestMapping("/uploadCoverImage")
    public Map<String,Object> uploadCoverImage(MultipartFile coverImage)throws Exception{
        System.out.println("filename:"+coverImage.getName());
        Map<String,Object> resultMap=new HashMap<>();
        if(!coverImage.isEmpty()){
            // 获取文件名
            String originalFilename = coverImage.getOriginalFilename();
            String suffixName=originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName= DateUtil.getCurrentDateStr()+suffixName;
            FileUtils.copyInputStreamToFile(coverImage.getInputStream(),new File(coverImagesFilePath+newFileName));
            resultMap.put("code",0);
            resultMap.put("msg","上传成功");
            resultMap.put("coverImageFileName",newFileName);
        }
        return resultMap;
    }

    /**
     * 上传投票选项图片
     * @param voteItemImage
     * @return
     * @throws Exception
     */
    @RequestMapping("/uploadVoteItemImage")
    public Map<String,Object> uploadVoteItemImage(MultipartFile voteItemImage)throws Exception{
        System.out.println("filename:"+voteItemImage.getName());
        Map<String,Object> resultMap=new HashMap<>();
        if(!voteItemImage.isEmpty()){
            // 获取文件名
            String originalFilename = voteItemImage.getOriginalFilename();
            String suffixName=originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName= DateUtil.getCurrentDateStr()+suffixName;
            FileUtils.copyInputStreamToFile(voteItemImage.getInputStream(),new File(voteItemImagesFilePath+newFileName));
            resultMap.put("code",0);
            resultMap.put("msg","上传成功");
            resultMap.put("voteItemImageFileName",newFileName);
        }
        return resultMap;
    }

    /**
     * 添加投票
     * @param vote
     * @param token
     * @return
     */
    @RequestMapping("/add")
    @Transactional
    public R add(@RequestBody Vote vote, @RequestHeader String token){
        Claims claims = JwtUtils.validateJWT(token).getClaims();
        vote.setOpenid(claims.getId());
        voteService.save(vote);
        List<VoteItem> voteItemList = vote.getVoteItemList();
        for(VoteItem voteItem:voteItemList){
            voteItem.setVoteId(vote.getId());
            voteItem.setNumber(0);
            voteItemService.save(voteItem);
        }
        return R.ok();
    }

    /**
     * 获取指定用户创建的投票列表
     * @param token
     * @return
     */
    @RequestMapping("/listOfUser")
    public R listOfUser(@RequestHeader String token){
        Claims claims = JwtUtils.validateJWT(token).getClaims();
        List<Vote> voteList = voteService.list(new QueryWrapper<Vote>().eq("openid", claims.getId()).orderByDesc("vote_end_time"));
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("voteList",voteList);
        return R.ok(resultMap);
    }

    /**
     * 获取指定用户参与的投票
     * @param token
     * @return
     */
    @RequestMapping("/listOfJoinUser")
    public R listOfJoinUser(@RequestHeader String token){
        Claims claims = JwtUtils.validateJWT(token).getClaims();
        List<Vote> voteList = voteService.list(new QueryWrapper<Vote>().inSql("id", "SELECT vote_id FROM t_vote_detail WHERE openid=" + claims.getId() + "'").orderByDesc("vote_end_time"));
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("voteList",voteList);
        return R.ok(resultMap);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R findById(@PathVariable(value = "id") Integer id){
        Vote vote = voteService.getById(id);
        WxUserInfo wxUserInfo = wxUserInfoService.getOne(new QueryWrapper<WxUserInfo>().eq("openid", vote.getOpenid()));
        vote.setWxUserInfo(wxUserInfo);
        List<VoteItem> voteItemList = voteItemService.list(new QueryWrapper<VoteItem>().eq("vote_id", id));
        vote.setVoteItemList(voteItemList);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("vote",vote);
        return R.ok(resultMap);
    }

    /**
     * 删除指定id的投票帖子
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    @Transactional
    public R delete(@PathVariable(value = "id") Integer id){
        voteDetailService.remove(new QueryWrapper<VoteDetail>().eq("vote_id",id));
        voteItemService.remove(new QueryWrapper<VoteItem>().eq("vote_id",id));
        voteService.removeById(id);
        return R.ok();
    }
}
