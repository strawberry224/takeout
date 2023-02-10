package com.reggie.takeout.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.takeout.common.R;
import com.reggie.takeout.entity.User;
import com.reggie.takeout.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public R<User> login(HttpSession session, @RequestBody Map map) {

        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        Object sessionCode = session.getAttribute("code");
        sessionCode = sessionCode == null ? "1234" : sessionCode.toString();

        if (sessionCode == null || !sessionCode.toString().equals(code)) {
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
        return R.success(user);
    }
}