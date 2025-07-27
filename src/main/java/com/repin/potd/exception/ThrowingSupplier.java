package com.repin.potd.exception;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Exception;
}
