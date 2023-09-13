package de.firecreeper82.exceptions.exceptions;

import de.firecreeper82.exceptions.CustomException;

public class SomethingWentWrongException extends CustomException {
    public SomethingWentWrongException(String message) {
        super(message);
    }
}
