package com.github.unldenis.fastorm4j.result;

import java.util.*;

public interface FAbstractResult<E extends Exception> {

    boolean isOk();

    boolean isErr();

    Optional<E> err();

    E unwrapErr();
}
