package com.github.unldenis.fastorm4j.compiler;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.List;

// Based on: http://javapracs.blogspot.cz/2011/06/dynamic-in-memory-compilation-using.html
public class DynamicCompiler {

    private JavaFileManager fileManager;
    private final String fullName;
    private final String sourceCode;

    public DynamicCompiler(String fullName, String srcCode) {
        this.fullName = fullName;
        this.sourceCode = srcCode;
        this.fileManager = initFileManager();
    }

    public JavaFileManager initFileManager() {
        if (fileManager == null) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            fileManager = new ClassFileManager(compiler
                    .getStandardFileManager(null, null, null));
        }
        return fileManager;
    }

    public void compile() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        List<JavaFileObject> files = new ArrayList<>();
        files.add(new CharSequenceJavaFileObject(fullName, sourceCode));

        compiler.getTask(
                null,
                fileManager,
                null,
                null,
                null,
                files
        ).call();
    }

    public Class<?> loadClass() throws ClassNotFoundException {
        return fileManager
                .getClassLoader(null)
                .loadClass(fullName);
    }

    public static class CharSequenceJavaFileObject extends SimpleJavaFileObject {

        /**
         * CharSequence representing the source code to be compiled
         */
        private final CharSequence content;

        public CharSequenceJavaFileObject(String className, CharSequence content) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = content;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }

    public static class ClassFileManager extends ForwardingJavaFileManager {
        private JavaClassObject javaClassObject;

        public ClassFileManager(StandardJavaFileManager standardManager) {
            super(standardManager);
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return new SecureClassLoader() {
                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    byte[] b = javaClassObject.getBytes();
                    return super.defineClass(name, javaClassObject.getBytes(), 0, b.length);
                }
            };
        }

        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            this.javaClassObject = new JavaClassObject(className, kind);
            return this.javaClassObject;
        }
    }

    public static class JavaClassObject extends SimpleJavaFileObject {
        protected final ByteArrayOutputStream bos =
                new ByteArrayOutputStream();

        public JavaClassObject(String name, Kind kind) {
            super(URI.create("string:///" + name.replace('.', '/')
                    + kind.extension), kind);
        }

        public byte[] getBytes() {
            return bos.toByteArray();
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return bos;
        }
    }
}
