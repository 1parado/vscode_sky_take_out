package com.sky.service.impl;

import com.fasterxml.jackson.databind.JsonSerializable.Base;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.entity.Employee.EmployeeBuilder;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void save(EmployeeDTO employeeDTO) {
        // System.out.println("当前线程id："+Thread.currentThread().getId());
        
        // 新增员工
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);// DTO转entity 将employeeDTO的属性值拷贝到employee
        employee.setStatus(StatusConstant.ENABLE);// 设置员工状态为启用
        
        // TODO md5加密 默认密码123456
        String password = DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes());
        employee.setPassword(password);

        // 设置创建时间和更新时间
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());
        // 设置创建人和修改人（通过动态方式获得当前已登录用户的id） 可以通过token解析出当前用户的id
        Long currentId = BaseContext.getCurrentId();
        employee.setCreateUser(currentId); //long型数据 后面需要加一个L
        employee.setUpdateUser(currentId);
        employeeMapper.insert(employee);
    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // startPage(int pageNum, int pageSize) 参数 页数 和每页显示条数 
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
         //总记录数
        List<Employee> records = page.getResult(); //当前页结果集
        PageResult pageResult = new PageResult(total, records);
        return pageResult;
       
    }

    @Override
    public void startOrStop(Integer status, Long id) {
       Employee employee = Employee.builder().status(status).id(id).build(); //
        employeeMapper.update(employee);
    }

    // 根据id查询用户信息
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        employee.setPassword("123456");
        return employee;    
    }

    // 更新(编辑)员工信息
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        // employee.setUpdateTime(LocalDateTime.now());
        // employee.setUpdateUser(BaseContext.getCurrentId()); // BaseContext可以获得当前线程的id
        employeeMapper.update(employee);
    }

}
