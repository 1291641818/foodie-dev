package com.personal.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.personal.enums.CommentLevel;
import com.personal.enums.YesOrNo;
import com.personal.mapper.*;
import com.personal.pojo.*;
import com.personal.pojo.vo.CommentLevelCountsVO;
import com.personal.pojo.vo.ItemCommentVO;
import com.personal.pojo.vo.SearchItemsVO;
import com.personal.pojo.vo.ShopcartVO;
import com.personal.service.ItemService;
import com.personal.utils.DesensitizationUtil;
import com.personal.utils.PagedGridResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-24 10:20
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Resource
    private ItemsMapper itemsMapper;

    @Resource
    private ItemsImgMapper itemsImgMapper;

    @Resource
    private ItemsSpecMapper itemsSpecMapper;

    @Resource
    private ItemsParamMapper itemsParamMapper;

    @Resource
    private ItemsCommentsMapper itemsCommentsMapper;

    @Resource
    private ItemsMapperCustom itemsMapperCustom;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ItemsImg> queryItemImgList(String itemId) {
        Example itemsImgExp = new Example(ItemsImg.class);
        itemsImgExp.createCriteria().andEqualTo("itemId", itemId);
        return itemsImgMapper.selectByExample(itemsImgExp);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example itemsSpecExp = new Example(ItemsSpec.class);
        itemsSpecExp.createCriteria().andEqualTo("itemId", itemId);
        return itemsSpecMapper.selectByExample(itemsSpecExp);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public ItemsParam queryItemParam(String itemId) {
        Example itemsParamExp = new Example(ItemsSpec.class);
        itemsParamExp.createCriteria().andEqualTo("itemId", itemId);
        return itemsParamMapper.selectOneByExample(itemsParamExp);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public CommentLevelCountsVO queryCommentLevelCounts(String itemId) {
        //??????????????????
        Integer goodCounts = getCommentCounts(itemId, CommentLevel.GOOD.getCode());
        Integer normalCounts = getCommentCounts(itemId, CommentLevel.NORMAL.getCode());
        Integer badCounts = getCommentCounts(itemId, CommentLevel.BAD.getCode());
        Integer totalCounts = goodCounts + normalCounts + badCounts;
        return CommentLevelCountsVO.builder()
                .goodCounts(goodCounts)
                .normalCounts(normalCounts)
                .badCounts(badCounts)
                .totalCounts(totalCounts)
                .build();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    Integer getCommentCounts(String itemId, Integer level) {
        ItemsComments condition = new ItemsComments();
        condition.setItemId(itemId);
        if (level == null) {
            condition.setCommentLevel(level);
        }

        return itemsCommentsMapper.selectCount(condition);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedGridResult queryPagedComments(String itemId, Integer level,
                                              Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("level", level);

        // mybatis-pagehelper

        //page: ?????????
        //pageSize: ??????????????????
        PageMethod.startPage(page, pageSize);

        List<ItemCommentVO> list = itemsMapperCustom.queryItemComments(map);
        list.stream().forEach(i -> i.setNickname(DesensitizationUtil.commonDisplay(i.getNickname())));
        return setterPagedGrid(list, page);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);
        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItems(map);
        return setterPagedGrid(list, page);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedGridResult searchItems(Integer catId, String sort, Integer page, Integer pageSize) {

        Map<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);
        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItemsByThirdCat(map);
        return setterPagedGrid(list, page);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<ShopcartVO> queryItemsBySpecIds(String specIds) {
        String[] ids = specIds.split(",");
        List<String> specIdsList = new ArrayList<>();
        //??????Collections.addAll???????????????list
        Collections.addAll(specIdsList, ids);
        return itemsMapperCustom.queryItemsBySpecIds(specIdsList);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public ItemsSpec queryItemSpecById(String specId) {
        return itemsSpecMapper.selectByPrimaryKey(specId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public String queryItemMainImgById(String itemId) {
        ItemsImg itemsImg = new ItemsImg();
        itemsImg.setItemId(itemId);
        itemsImg.setIsMain(YesOrNo.YES.getCode());
        ItemsImg result = itemsImgMapper.selectOne(itemsImg);
        return result != null ? result.getUrl() : "";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void decreaseItemSpecStock(String specId, int buyCounts) {

        // synchronized ????????????????????????????????????????????????
        // ????????????: ???????????????????????????????????????
        // ???????????? zookeeper redis

        // lockUtil.getLock(); -- ??????

        // 1. ????????????
//        int stock = 10;

        // 2. ????????????????????????????????????0??????
//        if (stock - buyCounts < 0) {
        // ????????????????????????
//            10 - 3 -3 - 5 = -1
//        }

        // lockUtil.unLock(); -- ??????


        int result = itemsMapperCustom.decreaseItemSpecStock(specId, buyCounts);
        if (result != 1) {
            throw new RuntimeException("??????????????????????????????????????????!");
        }
    }

    private PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = PagedGridResult
                .builder()
                .page(page)
                .rows(list)
                .total(pageList.getPages())
                .records(pageList.getTotal())
                .build();
        return grid;
    }
}
