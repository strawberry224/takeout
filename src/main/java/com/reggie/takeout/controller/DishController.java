package com.reggie.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.takeout.common.R;
import com.reggie.takeout.dto.DishDto;
import com.reggie.takeout.entity.Category;
import com.reggie.takeout.entity.Dish;
import com.reggie.takeout.entity.DishFlavor;
import com.reggie.takeout.service.CategoryService;
import com.reggie.takeout.service.DishFlavorService;
import com.reggie.takeout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author shenlijia
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dish) {
        dishService.saveWithFlavor(dish);
        redisTemplate.keys("dish:*").forEach(key -> redisTemplate.delete(key));
        return R.success("新增菜品成功");
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dish) {
        dishService.updateWithFlavor(dish);
        redisTemplate.keys("dish:*").forEach(key -> redisTemplate.delete(key));
        return R.success("更新菜品成功");
    }

    @GetMapping
    public R<DishDto> getDishById(@RequestParam("id") Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        Long categoryId = dish.getCategoryId();
        String key = "dish:" + categoryId;

        Object dishes = redisTemplate.opsForValue().get(key);
        if (dishes != null) {
            return R.success((List<DishDto>) dishes);
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> collect = list.stream().map(item -> {
            Long dishId = item.getId();

            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, dishId);
            wrapper.orderByAsc(DishFlavor::getUpdateTime);

            List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            dishDto.setFlavors(dishFlavors);

            return dishDto;

        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(key, collect, 60, TimeUnit.MINUTES);
        return R.success(collect);
    }

    @GetMapping("/page")
    public R<Page<DishDto>> getDishList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "size", defaultValue = "10") Integer size,
                                        @RequestParam(value = "name", required = false) String name) {
        Page<Dish> pageInfo = new Page<>(page, size);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, queryWrapper);
        BeanUtils.copyProperties(dishDtoPage, pageInfo, "records");

        List<DishDto> dishDtoList = pageInfo.getRecords().stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);

            Long categoryId = dish.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }
}
