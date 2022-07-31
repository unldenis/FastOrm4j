package com.github.unldenis.fastorm4j.result;

import java.util.*;

public interface FIntResult<E extends Exception> extends FAbstractResult<E> {

    static <E extends Exception> FIntResult<E> ok(final int ok) {
        return new Ok<>(ok);
    }

    static <E extends Exception> FIntResult<E> err(final E err) {
        return new Err<>(Objects.requireNonNull(err));
    }

    OptionalInt ok();

    int unwrap();

    int unwrapOr(final int def);

    int expect(final String msg);

    final class Ok<E extends Exception> implements FIntResult<E> {
        private final int value;

        public Ok(final int value) {
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
        public OptionalInt ok() {
            return OptionalInt.of(value);
        }

        @Override
        public int unwrap() {
            return value;
        }

        @Override
        public int unwrapOr(int def) {
            return value;
        }

        @Override
        public int expect(String msg) {
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
            return "Ok(%d)".formatted(value);
        }
    }

    final class Err<E extends Exception> implements FIntResult<E> {
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
        public OptionalInt ok() {
            return OptionalInt.empty();
        }

        @Override
        public int unwrap() {
            throw new UnsupportedOperationException(value);
        }

        @Override
        public int unwrapOr(final int def) {
            return def;
        }

        @Override
        public int expect(String msg) {
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
