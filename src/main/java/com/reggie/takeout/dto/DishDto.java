package com.reggie.takeout.dto;

import com.reggie.takeout.entity.Dish;
import com.reggie.takeout.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();
    private String categoryName;
    private Integer copies;

}
