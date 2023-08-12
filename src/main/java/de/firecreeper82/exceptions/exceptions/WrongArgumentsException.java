package de.firecreeper82.exceptions.exceptions;

import de.firecreeper82.exceptions.CustomException;

public class WrongArgumentsException extends CustomException {
    public WrongArgumentsException(String message) {
        super(message);
    }
}
