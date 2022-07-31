package com.github.unldenis.fastorm4j;

import com.github.unldenis.fastorm4j.ann.*;
import com.github.unldenis.fastorm4j.compiler.*;
import com.github.unldenis.fastorm4j.operation.*;
import com.github.unldenis.fastorm4j.result.*;

import java.lang.reflect.*;
import java.util.*;

import static com.github.unldenis.fastorm4j.result.FResult.err;
import static com.github.unldenis.fastorm4j.result.FResult.ok;

public interface FListener {

    FConnection connection();

    final class Factory {
        public static <T extends FListener> FResult<Class<T>, ClassNotFoundException> compile(Class<T> listener) {
            String subClassName = listener.getName() + "Impl";
            String subClassSimpleName = listener.getSimpleName() + "Impl";

            StringJoiner methods = new StringJoiner("\n");

            for(Method m: listener.getDeclaredMethods()) {
                m.setAccessible(true);

                if(Modifier.isStatic(m.getModifiers()))
                    continue;

                if(!m.isAnnotationPresent(Operation.class))
                    continue;

                var op = m.getAnnotation(Operation.class);


                String methodStr = switch (op.type()) {
                    case INSERT ->
                        new InsertOperation(m).parse();
                    default ->
                        throw new RuntimeException();
                };

                methods.add(methodStr);

            }

            String subclass = """
                    import com.github.unldenis.fastorm4j.*;
                    import com.github.unldenis.fastorm4j.result.*;
                    import java.sql.SQLException;

                    public final class %s extends %s {
                        %s
                    }
                    """.formatted(
                    subClassSimpleName,
                    listener.getSimpleName(),
                    methods.toString()
            );

            // add package if present
            if(!listener.getPackageName().isEmpty()) {
                subclass = "package %s;".formatted(listener.getPackageName()) + subclass;
            }

            System.out.println(subclass);

            DynamicCompiler dynamicCompiler = new DynamicCompiler(subClassName, subclass);
            dynamicCompiler.compile();
            try {
                return ok((Class<T>) dynamicCompiler.loadClass());
            } catch (ClassNotFoundException e) {
                return err(e);
            }
        }

        public static <T extends FListener> FResult<T, Exception> newInstance(Class<T> listener, Class<?>[] parameterTypes, Object... initargs) {
            var res = compile(listener);
            if(res.isErr())
                return err(res.unwrapErr());
            var cl = res.unwrap();

            try {
                return ok(cl.getConstructor(parameterTypes).newInstance(initargs));
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                return err(e);
            }
        }

        public static <T extends FListener> FResult<T, Exception> newInstance(Class<T> listener) {
            return newInstance(listener, new Class[0]);
        }
    }
}
