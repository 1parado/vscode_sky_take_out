package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    // 解决用户username重复注册问题
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        
        // Duplicate entry 'admin3' for key 'idx_username'
        String message = ex.getMessage();
        if ( message.contains("Duplicate entry")) {
            // 是用户名重复异常
        String[] split = message.split(" "); // Duplicate entry 'admin3' for key 'idx_username' 转换成数组
        String username = split[2];
        String msg = username + MessageConstant.ALREADY_EXISTS;
        return Result.error(msg);
        } else {
            // 不是该异常，交给上面的异常处理器处理
            return exceptionHandler(new BaseException(MessageConstant.UNKNOWN_ERROR));
            
        }
      
       
        



    }


}
