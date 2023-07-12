package com.keda.common.vaild;


import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {ListValueConstraintValidator.class}
)
public @interface ListValue {
    String message() default "参数值不满足范围";

    Class<?>[] groups() default {};

    int[] vals() default {};

    Class<? extends Payload>[] payload() default {};


}
