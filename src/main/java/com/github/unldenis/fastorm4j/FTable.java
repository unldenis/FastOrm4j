package com.github.unldenis.fastorm4j;

import com.github.unldenis.fastorm4j.result.*;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.stream.*;

import static com.github.unldenis.fastorm4j.result.FResult.err;
import static com.github.unldenis.fastorm4j.result.FResult.ok;

public record FTable(FConnection connection, String creationSql, String name, List<Column> columns) {

    public FAbstractResult<SQLException> openTable() {
        var res = connection.createStatement();
        if(res.isErr()) return res;

        FStatement stmt = res.unwrap();
        return stmt.execute(creationSql);
    }

    public record Column(String name, String type, boolean required) {
        @Override
        public String toString() {
            return name + " " + type + (required ? " NOT NUll" : "");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static FResult<Builder, IllegalArgumentException> builder(String tableName, Class<?> cl) {
        var builder = builder();
        builder.name(tableName);

        for (var field : cl.getDeclaredFields()) {
            field.setAccessible(true);

            if (Modifier.isStatic(field.getModifiers()))
                continue;

            try {
                String nameMethod = cl.isRecord() ? field.getName() : "get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);

                Method m = cl.getDeclaredMethod(nameMethod);

                if (!Modifier.isPublic(m.getModifiers()))
                    continue;

                if (Modifier.isStatic(m.getModifiers()))
                    continue;


                if(field.isAnnotationPresent(com.github.unldenis.fastorm4j.ann.Column.Id.class)) {
                    builder.columnID(field.getName());
                } else {
                    String name;
                    String type;
                    boolean required;

                    if(field.isAnnotationPresent(com.github.unldenis.fastorm4j.ann.Column.class)) {
                        var column = field.getAnnotation(com.github.unldenis.fastorm4j.ann.Column.class);
                        name = column.name().isEmpty() ? field.getName() : column.name();
                        if(column.type().isEmpty()) {

                            // Check any errors with sql type
                            var res = getSQlTypeFromClass(field.getType());
                            if(res.isErr())
                                return err(res.unwrapErr());
                            type = res.unwrap();

                        } else {
                            type = column.type();
                        }

                        required = column.required();
                    } else {
                        name = field.getName();

                        // Check any errors with sql type
                        var res = getSQlTypeFromClass(field.getType());
                        if(res.isErr())
                            return err(res.unwrapErr());
                        type = res.unwrap();

                        required = false;
                    }

                    builder.column(name, type, required);
                }

            } catch (NoSuchMethodException ignored) {
            }
        }

        return ok(builder);
    }

    private static FResult<String, IllegalArgumentException> getSQlTypeFromClass(Class<?> type) {
        if (Integer.class.equals(type) || int.class.equals(type)) {
            return ok("INTEGER");
        } else if (String.class.equals(type)) {
            return ok("TEXT");
        } else if (Long.class.equals(type) || long.class.equals(type)) {
            return ok("INTEGER");
        }
        return err( new IllegalArgumentException("Invalid SQLType from " + type.getName()));
    }

    public static class Builder {

        private String name;

        private final List<Column> columns = new ArrayList<>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder columnID(String name) {
            return column(name, "INT PRIMARY KEY", true);
        }

        public Builder column(String name, String type, boolean required) {
            this.columns.add(new Column(name, type, required));
            return this;
        }

        public FResult<FTable, IllegalArgumentException> build(FConnection connection) {
            if(name == null)
                return err(new IllegalArgumentException("Name not set"));
            if(columns.isEmpty())
                return err(new IllegalArgumentException("Columns are empty"));

            String types = columns
                    .stream()
                    .map(Column::toString)
                    .collect(Collectors.joining(", "));

            String sql =
                    """
                    CREATE TABLE IF NOT EXISTS %s (%s);
                    """.formatted(name, types);

            return ok(new FTable(connection, sql, name, columns));
        }
    }
}
