package com.reggie.takeout.dto;

import com.reggie.takeout.entity.Setmeal;
import com.reggie.takeout.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;
    private String categoryName;
}
