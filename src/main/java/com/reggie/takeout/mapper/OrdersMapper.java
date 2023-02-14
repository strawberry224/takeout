package com.reggie.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.takeout.entity.Orders;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author shenlijia
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
