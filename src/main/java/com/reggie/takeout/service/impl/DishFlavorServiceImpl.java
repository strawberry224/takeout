package com.reggie.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.takeout.entity.DishFlavor;
import com.reggie.takeout.mapper.DishFlavorMapper;
import com.reggie.takeout.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * @author shenlijia
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DishFlavorService {
}
