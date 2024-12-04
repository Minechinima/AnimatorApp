package com.minechinima.model.utils;


import java.lang.annotation.*;

/**
 * Document when a method does not perform any operations that could theoretically throw an exception.
 * OR when a method lets the app crash with an error i.e. is safe to use from a data integrity standpoint,
 * as the data will not be saved.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface NoExceptions {
}
