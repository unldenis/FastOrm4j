package com.github.unldenis.fastorm4j.ann;

import com.github.unldenis.fastorm4j.operation.*;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(value= METHOD)
public @interface Operation {

    String table();

    OperationType type();

}


