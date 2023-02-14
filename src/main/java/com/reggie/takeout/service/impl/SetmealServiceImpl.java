package com.reggie.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.takeout.common.CustomException;
import com.reggie.takeout.dto.SetmealDto;
import com.reggie.takeout.entity.Setmeal;
import com.reggie.takeout.entity.SetmealDish;
import com.reggie.takeout.mapper.SetmealMapper;
import com.reggie.takeout.service.SetmealDishService;
import com.reggie.takeout.service.SetmealService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shenlijia
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Resource
    private SetmealDishService setmealDishService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        Long setmealId = setmealDto.getId();

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().peek(
                setmealDish -> setmealDish.setSetmealId(setmealId)
        ).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<Setmeal>()
                .in(Setmeal::getId, ids)
                .eq(Setmeal::getStatus, 1);

        if (this.count(queryWrapper) > 0) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        this.removeBatchByIds(ids);

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<SetmealDish>()
                .in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }
}
