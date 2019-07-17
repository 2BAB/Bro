package me.xx2bab.bro.sample.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Documented
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireMultiValues {

    int value();
    String value1();
    long value2();
    char value3();
    boolean value4();

}
