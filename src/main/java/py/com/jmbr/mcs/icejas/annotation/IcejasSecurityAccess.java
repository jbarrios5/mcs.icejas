package py.com.jmbr.mcs.icejas.annotation;

import py.com.jmbr.java.commons.context.OperationAllow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface IcejasSecurityAccess {
    OperationAllow operation() default OperationAllow.VERIFY_TOKEN;
}
