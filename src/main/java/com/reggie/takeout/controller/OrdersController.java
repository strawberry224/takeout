package com.reggie.takeout.controller;


import com.reggie.takeout.common.R;
import com.reggie.takeout.entity.Orders;
import com.reggie.takeout.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @author shenlijia
 */
@Slf4j
@RestController
@RequestMapping("order")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    @PostMapping("submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("submit: {}", orders);
        ordersService.submit(orders);
        return R.success("提交成功");
    }
}