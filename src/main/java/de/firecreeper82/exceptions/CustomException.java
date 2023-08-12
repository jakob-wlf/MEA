package de.firecreeper82.exceptions;

public abstract class CustomException extends Exception{

    protected String message;

    public CustomException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
