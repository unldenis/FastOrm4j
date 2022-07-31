package com.github.unldenis.fastorm4j.result;

import java.util.*;

public interface FBoolResult<E extends Exception> extends FAbstractResult<E> {

    static <E extends Exception> FBoolResult<E> ok(final boolean ok) {
        return new Ok<>(ok);
    }

    static <E extends Exception> FBoolResult<E> err(final E err) {
        return new Err<>(Objects.requireNonNull(err));
    }

    Optional<Boolean> ok();

    boolean unwrap();

    boolean unwrapOr(final boolean def);

    boolean expect(final String msg);

    final class Ok<E extends Exception> implements FBoolResult<E> {
        private final boolean value;

        public Ok(final boolean value) {
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
        public Optional<Boolean> ok() {
            return Optional.of(value);
        }

        @Override
        public boolean unwrap() {
            return value;
        }

        @Override
        public boolean unwrapOr(boolean def) {
            return value;
        }

        @Override
        public boolean expect(String msg) {
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
            Ok<?> ok = (Ok<?>) o;
            return value == ok.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Ok(%b)".formatted(value);
        }
    }

    final class Err<E extends Exception> implements FBoolResult<E> {
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
        public Optional<Boolean> ok() {
            return Optional.empty();
        }

        @Override
        public boolean unwrap() {
            throw new UnsupportedOperationException(value);
        }

        @Override
        public boolean unwrapOr(final boolean def) {
            return def;
        }

        @Override
        public boolean expect(String msg) {
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
            Err<?> err = (Err<?>) o;
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
