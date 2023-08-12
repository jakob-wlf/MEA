package de.firecreeper82.exceptions.exceptions;

import de.firecreeper82.exceptions.CustomException;

public class CommandNotFoundException extends CustomException {
    public CommandNotFoundException(String message) {
        super(message);
    }
}

