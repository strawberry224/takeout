package com.reggie.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.takeout.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shenlijia
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
