package com.github.unldenis.fastorm4j.util;


import java.sql.*;

@FunctionalInterface
public interface SafeSQLSupplier<F extends Wrapper, T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get(F wrapper) throws SQLException;
}
