package com.keda.gulimall.ware.filter;

import com.keda.common.Biz.BizcodeEnum;
import com.keda.common.utils.R;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;

/**
 * @author Jmd
 * @create 2024-03-2024/3/25-22:42
 * @Description：
 */

@RestControllerAdvice // 抓取所有controller方法抛出的异常并进行处理，并将异常处理方法的方法值，以json的方法返回
// @RestControllerAdvice注解等于@ControllerAdvice和@ResponseBody注解的组合
@Order(100000)
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class) // @Validated验证失败时抛出的是MethodArgumentNotValidException异常
    // 早先是BindException，后修改为了MethodArgumentNotValidException
    public R validExceptionHandle(MethodArgumentNotValidException exception){
        //System.out.println("test");
        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> errors = bindingResult.getFieldErrors();

        // 遍历获取该异常中的所有错误信息并封装到Map中
        HashMap<String, String> errorsInfoMap = new HashMap<>();
        errors.forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();

            errorsInfoMap.put(field,message);
        });

        return R.error(BizcodeEnum.VAILD_Exception.getCode(), BizcodeEnum.VAILD_Exception.getMsg()).put("data", errorsInfoMap);
    }

    @ExceptionHandler(RuntimeException.class) // @Validated验证失败时抛出的是RuntimeException异常
    // 早先是BindException，后修改为了MethodArgumentNotValidException
    public R validExceptionHandle(RuntimeException exception){
        //System.out.println("test");

        String message = exception.getMessage();

        return R.error(BizcodeEnum.Runtime_Exception.getCode(), BizcodeEnum.Runtime_Exception.getMsg()).put("data", message);
    }
}
