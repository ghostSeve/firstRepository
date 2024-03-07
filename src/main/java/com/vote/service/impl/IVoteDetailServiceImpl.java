package com.vote.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vote.entity.VoteDetail;
import com.vote.mapper.VoteDetailMapper;
import com.vote.service.IVoteDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 投票详情Service实现类
 */
@Service("voteDetailService")
public class IVoteDetailServiceImpl extends ServiceImpl<VoteDetailMapper, VoteDetail> implements IVoteDetailService {

    @Autowired
    private VoteDetailMapper voteDetailMapper;
}
