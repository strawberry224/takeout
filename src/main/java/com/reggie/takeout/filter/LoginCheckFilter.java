package com.reggie.takeout.filter;


import com.alibaba.fastjson2.JSON;
import com.reggie.takeout.common.BasicContext;
import com.reggie.takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author shenlijia
 */
@Slf4j
@WebFilter(filterName = "LoginFilter", urlPatterns = {"/*"})
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        String[] urls = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/frontend/**",
                "/user/sendCode",
                "/user/login",
        };

        boolean check = check(urls, requestURI);
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }

        Object employeeId = request.getSession().getAttribute("employee");
        if (employeeId != null) {
            BasicContext.setCurrentId((Long) employeeId);
            filterChain.doFilter(request, response);
            return;
        }

        Object userId = request.getSession().getAttribute("user");
        if (userId != null) {
            BasicContext.setCurrentId((Long) userId);
            filterChain.doFilter(request, response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("NOT LOGIN")));
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
