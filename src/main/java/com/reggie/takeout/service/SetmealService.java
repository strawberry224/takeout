package com.reggie.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.takeout.dto.SetmealDto;
import com.reggie.takeout.entity.Setmeal;

import java.util.List;

/**
 * @author shenlijia
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 保存套餐
     *
     * @param setmealDto 套餐数据
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐
     *
     * @param ids 套餐ID集合
     */
    void removeWithDish(List<Long> ids);
}
