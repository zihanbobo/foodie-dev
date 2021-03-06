package com.imooc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.commom.enums.CommentLevel;
import com.imooc.commom.enums.YesOrNo;
import com.imooc.commom.utils.DesensitizationUtil;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.SearchItemsVO;
import com.imooc.pojo.vo.ShopcartVO;
import com.imooc.service.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 商品表 商品信息相关表：分类表，商品图片表，商品规格表，商品参数表(Items)表服务实现类
 *
 * @author 张启航
 * @since 2020-11-13 17:17:24
 */
@Service("itemsService")
public class ItemsServiceImpl extends ServiceImpl<ItemsMapper, Items> implements ItemsService {

    @Autowired
    private ItemsMapper itemsMapper;
    @Autowired
    private ItemsImgMapper itemsImgMapper;
    @Autowired
    private ItemsSpecMapper itemsSpecMapper;
    @Autowired
    private ItemsParamMapper itemsParamMapper;
    @Autowired
    private ItemsMapperCustom itemsMapperCustom;
    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemById(String itemId) {
        return itemsMapper.selectById(itemId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {
        QueryWrapper<ItemsImg> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id",itemId);
        return itemsImgMapper.selectList(queryWrapper);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        QueryWrapper<ItemsSpec> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id",itemId);
        return itemsSpecMapper.selectList(queryWrapper);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParam(String itemId) {
        QueryWrapper<ItemsParam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id",itemId);
        return itemsParamMapper.selectOne(queryWrapper);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountsVO queryCommentCounts(String itemId) {
        Integer goodCounts = getCommentCounts(itemId, CommentLevel.GOOD.type);
        Integer normalCounts = getCommentCounts(itemId, CommentLevel.NORMAL.type);
        Integer badCounts = getCommentCounts(itemId, CommentLevel.BAD.type);
        Integer totalCounts = goodCounts + normalCounts + badCounts;

        CommentLevelCountsVO countsVO = new CommentLevelCountsVO();
        countsVO.setTotalCounts(totalCounts);
        countsVO.setGoodCounts(goodCounts);
        countsVO.setNormalCounts(normalCounts);
        countsVO.setBadCounts(badCounts);

        return countsVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    Integer getCommentCounts(String itemId, Integer level) {
        QueryWrapper<ItemsComments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id",itemId);
        if (level != null) {
            queryWrapper.eq("comment_level",level);
        }
        return itemsCommentsMapper.selectCount(queryWrapper);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Page<ItemCommentVO> queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("itemId",itemId);
        map.put("level",level);

        Page<ItemCommentVO> itemCommentVOPage = new Page<>(page,pageSize);

        Page<ItemCommentVO> itemCommentVOPage1 = itemsMapperCustom.queryItemComments(map, itemCommentVOPage);

        //用户名匿名（imooc --> i***c）
        itemCommentVOPage1.getRecords().stream().forEach(itemCommentVO ->
                itemCommentVO.setNickname(DesensitizationUtil.commonDisplay(itemCommentVO.getNickname())));

        return itemCommentVOPage1;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Page<SearchItemsVO> searhItems(String keywords, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);

        Page<SearchItemsVO> page1 = new Page<>(page, pageSize);
        Page<SearchItemsVO> searchItemsVOPage = itemsMapperCustom.searchItems(map, page1);

        return searchItemsVOPage;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Page<SearchItemsVO> searhItems(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);

        Page<SearchItemsVO> page1 = new Page<>(page, pageSize);
        Page<SearchItemsVO> searchItemsVOPage = itemsMapperCustom.searchItemsByThirdCat(map, page1);

        return searchItemsVOPage;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopcartVO> queryItemsBySpecIds(String specIds) {

        String ids[] = specIds.split(",");
        List<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList, ids);

        return itemsMapperCustom.queryItemsBySpecIds(specIdsList);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsSpec queryItemSpecById(String specId) {
        return itemsSpecMapper.selectById(specId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemMainImgById(String itemId) {
        QueryWrapper<ItemsImg> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id",itemId);
        queryWrapper.eq("is_main",YesOrNo.YES.type);
        ItemsImg result = itemsImgMapper.selectOne(queryWrapper);
        return result != null ? result.getUrl() : "";
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void decreaseItemSpecStock(String specId, int buyCounts) {
        // synchronized 不推荐使用，集群下无用，性能低下
        // 锁数据库: 不推荐，导致数据库性能低下
        // 分布式锁 zookeeper redis

        // lockUtil.getLock(); -- 加锁

        // 1. 查询库存
//        int stock = 10;

        // 2. 判断库存，是否能够减少到0以下
//        if (stock - buyCounts < 0) {
        // 提示用户库存不够
//            10 - 3 -3 - 5 = -1
//        }

        // lockUtil.unLock(); -- 解锁


        int result = itemsMapperCustom.decreaseItemSpecStock(specId, buyCounts);
        if (result != 1) {
            throw new RuntimeException("订单创建失败，原因：库存不足!");
        }
    }
}