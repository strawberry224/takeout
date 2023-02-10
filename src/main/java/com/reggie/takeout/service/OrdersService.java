package com.reggie.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.takeout.entity.Orders;


public interface OrdersService extends IService<Orders> {

    void submit(Orders orders);

}

