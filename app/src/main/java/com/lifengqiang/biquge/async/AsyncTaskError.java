package com.lifengqiang.biquge.async;

public class AsyncTaskError {
    private String message;
    private Exception exception;

    public AsyncTaskError(String message, Exception exception) {
        this.message = message;
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public void print() {
        if (exception != null) {
            exception.printStackTrace();
        }
    }
}
