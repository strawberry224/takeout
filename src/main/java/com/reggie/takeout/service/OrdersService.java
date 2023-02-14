package com.reggie.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.takeout.entity.Orders;


/**
 * @author shenlijia
 */
public interface OrdersService extends IService<Orders> {

    /**
     * @param orders 订单信息
     */
    void submit(Orders orders);

}

