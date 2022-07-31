package com.github.unldenis.fastorm4j;

import com.github.unldenis.fastorm4j.ann.*;
import com.github.unldenis.fastorm4j.compiler.*;
import com.github.unldenis.fastorm4j.operation.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

public interface FListener {

    FConnection connection();

    static <T extends FListener> T compile(Class<T> listener, Object... initArgs) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] classes = Arrays.stream(initArgs).map(Object::getClass).toArray(Class[]::new);
        var cl = compile(listener, listener.getConstructor(classes));
        return cl.getConstructor(classes).newInstance(initArgs);
    }

    static <T extends FListener> Class<T> compile(Class<T> listener, Constructor<T> constructor) throws ClassNotFoundException {
        String subClassName = listener.getName() + "Impl";
        String subClassSimpleName = listener.getSimpleName() + "Impl";

        StringJoiner methods = new StringJoiner("\n");

        for (Method m : listener.getDeclaredMethods()) {
            m.setAccessible(true);

            if (Modifier.isStatic(m.getModifiers()))
                continue;

            if (!m.isAnnotationPresent(Operation.class))
                continue;

            var op = m.getAnnotation(Operation.class);


            String methodStr = switch (op.type()) {
                case INSERT -> new InsertOperation(m).parse();
                default -> throw new RuntimeException();
            };

            methods.add(methodStr);

        }
        AtomicInteger paramsIndex = new AtomicInteger(0);
        String constructorParameters = Arrays
                .stream(constructor.getParameterTypes())
                .map(p -> "%s arg%d".formatted(p.getName(), paramsIndex.getAndIncrement()))
                .collect(Collectors.joining(", "));

        String superCall = "";
        if (!constructorParameters.isEmpty()) {
            paramsIndex.set(0);
            superCall = Arrays
                    .stream(constructor.getParameterTypes())
                    .map(p -> "arg" + paramsIndex.getAndIncrement())
                    .collect(Collectors.joining(", "));
        }

        String exceptionsStr = Arrays
                .stream(constructor.getExceptionTypes())
                .map(Class::getName)
                .collect(Collectors.joining(","));
        if (!exceptionsStr.isEmpty()) {
            exceptionsStr = " throws " + exceptionsStr;
        }

        String constructorStr = """
                            public %s(%s)%s {
                                super(%s);
                            }
                        """.formatted(subClassSimpleName, constructorParameters, exceptionsStr, superCall);


        String subclass = """
                    import com.github.unldenis.fastorm4j.*;
                    import java.sql.*;

                    public final class %s extends %s {
                        
                        // Constructor
                    %s
                        // Operations
                    %s
                    }
                    """.formatted(
                subClassSimpleName,
                listener.getSimpleName(),
                constructorStr,
                methods.toString()
        );

        // add package if present
        if (!listener.getPackageName().isEmpty()) {
            subclass = "package %s;".formatted(listener.getPackageName()) + subclass;
        }

        System.out.println(subclass);

        DynamicCompiler dynamicCompiler = new DynamicCompiler(subClassName, subclass);
        dynamicCompiler.compile();
        return (Class<T>) dynamicCompiler.loadClass();
    }
}
