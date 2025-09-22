package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }
    //  新增员工
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO) {
        // System.out.println("当前线程id："+Thread.currentThread().getId());
        log.info("新增员工",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }
    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }
    // 分页查询方法
    @GetMapping("/page")
    public Result<PageResult> Page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询结果为：{}",employeePageQueryDTO);
        PageResult pageQuery = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageQuery);
    }

    // 启用和禁用员工 需要知道员工的状态和员工的id
    @PostMapping("status/{status}")
    public Result startOrStop(@PathVariable Integer status , Long id){
        log.info("员工的状态：{}，员工的id：{}",status,id);
        employeeService.startOrStop(status,id); //不需要返回值
        return Result.success();
    }
    // 根据员工id查询信息
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id){
        log.info("被查询员工的id：{}",id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee); //Result.success（参数） 会响应code = 1 参数的data

    }
    // 编辑员工信息
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息:{}",employeeDTO);
        employeeService.update(employeeDTO); // 更新信息无需返回值
        return Result.success();
    }

    

}
