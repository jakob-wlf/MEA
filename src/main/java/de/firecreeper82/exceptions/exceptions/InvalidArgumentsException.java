package de.firecreeper82.exceptions.exceptions;

import de.firecreeper82.exceptions.CustomException;

public class InvalidArgumentsException extends CustomException {
    public InvalidArgumentsException(String message) {
        super(message);
    }
}
