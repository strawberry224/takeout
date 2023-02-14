package com.reggie.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.takeout.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author shenlijia
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
