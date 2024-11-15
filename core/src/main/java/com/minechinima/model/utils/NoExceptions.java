package com.minechinima.model.utils;


import java.lang.annotation.*;

/**
 * Document when a method does not perform any operations that could theoretically throw an exception.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface NoExceptions {
}
