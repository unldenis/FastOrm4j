package com.github.unldenis.fastorm4j;

import com.github.unldenis.fastorm4j.result.*;
import com.github.unldenis.fastorm4j.util.Void;

import java.sql.*;

import static com.github.unldenis.fastorm4j.result.FResult.*;

public record FPreparedStatement(PreparedStatement statement) {

    protected static FPreparedStatement of(final PreparedStatement preparedStatement) {
        return new FPreparedStatement(preparedStatement);
    }

    public FResult<FResultSet, SQLException> executeQuery() {
        try {
            return ok(FResultSet.of(statement.executeQuery()));
        } catch (SQLException e) {
            return err(e);
        }
    }

    public FIntResult<SQLException> executeUpdate() {
        try {
            return FIntResult.ok(statement.executeUpdate());
        } catch (SQLException e) {
            return FIntResult.err(e);
        }
    }

    public FBoolResult<SQLException> execute() {
        try {
            return FBoolResult.ok(statement.execute());
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

    public FResult<Void, SQLException> setString(int index, String x) {
        try {
            statement.setString(index, x);
            return VOID_SQL_EXCEPTION;
        } catch (SQLException e) {
            return err(e);
        }
    }

    public FResult<Void, SQLException> setInt(int index, int x) {
        try {
            statement.setInt(index, x);
            return VOID_SQL_EXCEPTION;
        } catch (SQLException e) {
            return err(e);
        }
    }

    public FResult<Void, SQLException> setLong(int index, long x) {
        try {
            statement.setLong(index, x);
            return VOID_SQL_EXCEPTION;
        } catch (SQLException e) {
            return err(e);
        }
    }
}

