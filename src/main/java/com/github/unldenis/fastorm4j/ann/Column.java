package com.github.unldenis.fastorm4j.ann;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;

@Retention(RetentionPolicy.RUNTIME)
@Target(value= FIELD)
public @interface Column {

    String name() default  "";

    String type() default  "";

    boolean required() default false;


    /*
        ID for PRIMARY KEY
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(value= FIELD)
    public @interface Id {

    }

}
