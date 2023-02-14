package com.reggie.takeout.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.takeout.common.R;
import com.reggie.takeout.entity.User;
import com.reggie.takeout.service.UserService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author shenlijia
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CacheManager cacheManager;

    @Cacheable(value = "userCache", key = "#id")
    @GetMapping
    public R<User> getById(@RequestParam("id") Long id) {
        User user = userService.getById(id);
        return R.success(user);
    }

    @PostMapping("/login")
    public R<User> login(HttpSession session, @RequestBody Map map) {
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        redisTemplate.opsForValue().set(phone, "1234");
        Object smsCode = redisTemplate.opsForValue().get(phone);

        if (smsCode == null || !smsCode.toString().equals(code)) {
            R.error("验证码错误");
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = userService.getOne(queryWrapper);

        if (user == null) {
            user = new User();
            user.setPhone(phone);
            userService.save(user);
        }

        session.setAttribute("user", user.getId());
        redisTemplate.delete(phone);
        return R.success(user);
    }
}