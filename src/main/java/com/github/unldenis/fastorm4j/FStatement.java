package com.github.unldenis.fastorm4j;

import com.github.unldenis.fastorm4j.result.*;
import com.github.unldenis.fastorm4j.util.Void;

import java.sql.*;

import static com.github.unldenis.fastorm4j.result.FResult.*;

public record FStatement(Statement statement) {

    protected static FStatement of(final Statement statement) {
        return new FStatement(statement);
    }

    public FResult<FResultSet, SQLException> executeQuery(String query) {
        try {
            return ok(FResultSet.of(statement.executeQuery(query)));
        } catch (SQLException e) {
            return err(e);
        }
    }

    public FIntResult<SQLException> executeUpdate(String sql) {
        try {
            return FIntResult.ok(statement.executeUpdate(sql));
        } catch (SQLException e) {
            return FIntResult.err(e);
        }
    }

    public FBoolResult<SQLException> execute(String sql) {
        try {
            return FBoolResult.ok(statement.execute(sql));
        } catch (SQLException e) {
            return FBoolResult.err(e);
        }
    }

    public FResult<Void, SQLException> close() {
        try {
            statement.close();
            return VOID_SQL_EXCEPTION;
        } catch (SQLException e) {
            return err(e);
        }
    }
}
