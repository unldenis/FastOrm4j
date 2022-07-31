package com.github.unldenis.fastorm4j.operation;

import com.github.unldenis.fastorm4j.ann.*;

import java.lang.reflect.*;
import java.util.*;

public record InsertOperation(
        String _tableName,

        String _methodName,
        String _paramObjectName,
        Class<?> _param
) {

    public InsertOperation(Method method) {
        this(
                method.getAnnotation(Operation.class).table(),
                method.getName(),
                method.getParameters()[0].getName(),
                method.getParameters()[0].getType()
        );
    }

    public String parse() {
        StringJoiner fields = new StringJoiner(", ");
        StringJoiner setStatements = new StringJoiner("\n");
        setStatements.add("FPreparedStatement stmt = connection().prepareStatement(insertSQL).unwrap();");


        int index = 1;
        for (var field : _param.getDeclaredFields()) {
            field.setAccessible(true);

            if (Modifier.isStatic(field.getModifiers()))
                continue;

            try {
                String nameMethod = _param.isRecord() ? field.getName() : "get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);

                Method m = _param.getDeclaredMethod(nameMethod);

                if (!Modifier.isPublic(m.getModifiers()))
                    continue;

                if (Modifier.isStatic(m.getModifiers()))
                    continue;

                fields.add(field.getName());

                var type = field.getType();
                if (Integer.class.equals(type) || int.class.equals(type)) {
                    setStatements.add("        stmt.setInt(%d, %s.%s()).unwrap();".formatted(index++, _paramObjectName, nameMethod));
                } else if (String.class.equals(type)) {
                    setStatements.add("        stmt.setString(%d, %s.%s()).unwrap();".formatted(index++, _paramObjectName, nameMethod));
                } else if (Long.class.equals(type) || long.class.equals(type)) {
                    setStatements.add("        stmt.setLong(%d, %s.%s()).unwrap();".formatted(index++, _paramObjectName, nameMethod));
                }
            } catch (NoSuchMethodException ignored) {
            }
        }

        String fieldsStr = fields.toString();
        String anonFieldsStr = fieldsStr.replaceAll("[a-zA-Z]+", "?");


        String insertSQL = """
                String insertSQL = "INSERT INTO %s (%s) VALUES (%s)";""".formatted(_tableName, fieldsStr, anonFieldsStr);


        return """
                @Override
                    public FIntResult<SQLException> %s(%s %s) {
                        %s
                        %s
                        return stmt.executeUpdate();
                    }
                """.formatted(_methodName, _param.getName(), _paramObjectName, insertSQL, setStatements.toString());
    }
}
