package com.reggie.takeout.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.takeout.common.BasicContext;
import com.reggie.takeout.common.R;
import com.reggie.takeout.entity.Dish;
import com.reggie.takeout.entity.Setmeal;
import com.reggie.takeout.entity.ShoppingCart;
import com.reggie.takeout.service.DishService;
import com.reggie.takeout.service.SetmealService;
import com.reggie.takeout.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author shenlijia
 */
@Slf4j
@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;

    @PostMapping
    public R<ShoppingCart> save(@RequestBody ShoppingCart shoppingCart) {
        shoppingCart.setUserId(BasicContext.getCurrentId());

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());

        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        if (dishId != null) {
            wrapper.eq(ShoppingCart::getDishId, dishId);
            wrapper.eq(ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());

            Dish dish = dishService.getById(dishId);
            shoppingCart.setAmount(dish.getPrice());

        } else {
            wrapper.eq(ShoppingCart::getSetmealId, setmealId);

            Setmeal setmeal = setmealService.getById(setmealId);
            shoppingCart.setAmount(setmeal.getPrice());
        }

        ShoppingCart existed = shoppingCartService.getOne(wrapper);
        if (existed == null) {
            shoppingCartService.save(shoppingCart);
            return R.success(shoppingCart);
        }

        existed.setNumber(existed.getNumber() + 1);
        shoppingCartService.updateById(existed);

        return R.success(existed);
    }

    @DeleteMapping("clean")
    public R<String> remove() {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, BasicContext.getCurrentId());

        shoppingCartService.remove(wrapper);
        return R.success("清空购物车成功");
    }

    @GetMapping("list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BasicContext.getCurrentId());
        wrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(wrapper);
        return R.success(list);
    }
}