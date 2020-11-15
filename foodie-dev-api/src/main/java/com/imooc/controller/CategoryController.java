package com.imooc.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.imooc.pojo.Category;
import com.imooc.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 商品分类 (Category)表控制层
 *
 * @author 张启航
 * @since 2020-11-13 18:07:03
 */
@RestController
@RequestMapping("category")
public class CategoryController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private CategoryService categoryService;

    /**
     * 分页查询所有数据
     *
     * @param page     分页对象
     * @param category 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<Category> page, Category category) {
        return success(this.categoryService.page(page, new QueryWrapper<>(category)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.categoryService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param category 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody Category category) {
        return success(this.categoryService.save(category));
    }

    /**
     * 修改数据
     *
     * @param category 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody Category category) {
        return success(this.categoryService.updateById(category));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.categoryService.removeByIds(idList));
    }
}