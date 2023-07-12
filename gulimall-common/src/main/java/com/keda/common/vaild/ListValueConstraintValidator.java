package com.keda.common.vaild;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

// ListValue是标注在什么注解上面，第二个注解是这个校验器校验的是什么类型的字段
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    private int[] vals;

    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        this.vals = vals;
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {


        for (int value:vals){
            // 有相等的就返回校验成功
            if (value == integer.intValue()){
                return true;
            }
        }

        return false;
    }


}
