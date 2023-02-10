package com.reggie.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.takeout.common.BasicContext;
import com.reggie.takeout.common.CustomException;
import com.reggie.takeout.entity.AddressBook;
import com.reggie.takeout.entity.OrderDetail;
import com.reggie.takeout.entity.Orders;
import com.reggie.takeout.entity.ShoppingCart;
import com.reggie.takeout.mapper.OrdersMapper;
import com.reggie.takeout.service.AddressBookService;
import com.reggie.takeout.service.OrderDetailService;
import com.reggie.takeout.service.OrdersService;
import com.reggie.takeout.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private AddressBookService addressBookService;

    @Resource
    private OrderDetailService orderDetailService;

    @Transactional
    public void submit(Orders orders) {
        Long userId = BasicContext.getCurrentId();

        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);

        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空");
        }

        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("收货地址不存在");
        }

        long orderId = IdWorker.getId();
        AtomicInteger amount = calculateTotalAmount(shoppingCarts, orderId);

        orders.setId(orderId);
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setStatus(2);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        this.save(orders);

        shoppingCartService.remove(wrapper);
    }

    private AtomicInteger calculateTotalAmount(List<ShoppingCart> shoppingCarts, Long orderId) {
        AtomicInteger totalAmount = new AtomicInteger();


        List<OrderDetail> collect = shoppingCarts.stream().map(shoppingCart -> {
            OrderDetail orderDetail = new OrderDetail();

            BigDecimal amount = shoppingCart.getAmount();
            Integer number = shoppingCart.getNumber();

            totalAmount.addAndGet(amount.multiply(new BigDecimal(number)).intValue());

            orderDetail.setOrderId(orderId);
            orderDetail.setAmount(amount);
            orderDetail.setNumber(number);

            return orderDetail;
        }).collect(Collectors.toList());

        orderDetailService.saveBatch(collect);
        return totalAmount;
    }
}

