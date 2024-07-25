package com.itc.demo.utils;

import lombok.Getter;

@Getter
public abstract class ResultState<T> {
    private final T data;
    private final String message;

    protected ResultState(T data, String message) {
        this.data = data;
        this.message = message;
    }


    public static class Success<T> extends ResultState<T> {
        public Success(T data) {
            super(data, null);
        }
    }

    public static class Error<T> extends ResultState<T> {
        public Error(String message, T data) {
            super(data, message);
        }

        public Error(String message) {
            this(message, null);
        }
    }
}
