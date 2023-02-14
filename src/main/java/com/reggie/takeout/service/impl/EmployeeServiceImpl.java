package com.reggie.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.takeout.entity.Employee;
import com.reggie.takeout.mapper.EmployeeMapper;
import com.reggie.takeout.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author shenlijia
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
        implements EmployeeService {
}
