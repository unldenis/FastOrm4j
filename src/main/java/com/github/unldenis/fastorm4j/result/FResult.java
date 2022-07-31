package com.github.unldenis.fastorm4j.result;

import com.github.unldenis.fastorm4j.util.Void;

import java.sql.*;
import java.util.*;

public interface FResult<T, E extends Exception> extends FAbstractResult<E> {

    FResult<Void, SQLException> VOID_SQL_EXCEPTION = ok(Void.getInstance());

    FResult<Void, Exception> VOID_EXCEPTION = ok(Void.getInstance());

    static <T, E extends Exception> FResult<T, E> ok(final T ok) {
        return new Ok<T, E>(Objects.requireNonNull(ok));
    }

    static <T, E extends Exception> FResult<T, E> err(final E err) {
        return new Err<T, E>(Objects.requireNonNull(err));
    }

    Optional<T> ok();

    T unwrap();

    T unwrapOr(final T def);

    T expect(final String msg);

    final class Ok<T, E extends Exception> implements FResult<T, E> {
        private final T value;

        public Ok(final T value) {
            this.value = value;
        }

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public boolean isErr() {
            return false;
        }

        @Override
        public Optional<E> err() {
            return Optional.empty();
        }

        @Override
        public Optional<T> ok() {
            return Optional.of(value);
        }

        @Override
        public T unwrap() {
            return value;
        }

        @Override
        public T unwrapOr(T def) {
            return value;
        }

        @Override
        public T expect(String msg) {
            return value;
        }

        @Override
        public E unwrapErr() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Ok<?, ?> ok = (Ok<?, ?>) o;
            return value.equals(ok.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Ok(%s)".formatted(value);
        }
    }

    final class Err<T, E extends Exception> implements FResult<T, E> {
        private final E value;

        public Err(final E value) {
            this.value = value;
        }

        @Override
        public boolean isOk() {
            return false;
        }

        @Override
        public boolean isErr() {
            return true;
        }

        @Override
        public Optional<E> err() {
            return Optional.of(value);
        }

        @Override
        public Optional<T> ok() {
            return Optional.empty();
        }

        @Override
        public T unwrap() {
            throw new UnsupportedOperationException(value);
        }

        @Override
        public T unwrapOr(final T def) {
            return def;
        }

        @Override
        public T expect(String msg) {
            throw new UnsupportedOperationException(msg, value);
        }

        @Override
        public E unwrapErr() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Err<?, ?> err = (Err<?, ?>) o;
            return value.equals(err.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Err(%s)".formatted(value);
        }
    }

}
