package com.github.unldenis.fastorm4j;

import com.github.unldenis.fastorm4j.result.*;
import com.github.unldenis.fastorm4j.util.*;

import java.sql.*;

import static com.github.unldenis.fastorm4j.result.FResult.err;
import static com.github.unldenis.fastorm4j.result.FResult.ok;

public record FResultSet(ResultSet resultSet) {

    protected static FResultSet of(final ResultSet resultSet) {
        return new FResultSet(resultSet);
    }

    public <T> FResult<T, SQLException> safe(SafeSQLSupplier<ResultSet, T> supplier) {
        try {
            return ok(supplier.get(resultSet));
        } catch (SQLException e) {
            return err(e);
        }
    }

    public FBoolResult<SQLException> next() {
        try {
            return FBoolResult.ok(resultSet.next());
        } catch (SQLException e) {
            return FBoolResult.err(e);
        }
    }
}
