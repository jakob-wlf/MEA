package de.firecreeper82.exceptions.exceptions;

import de.firecreeper82.exceptions.CustomException;

public class RoleNoFoundException extends CustomException {
    public RoleNoFoundException(String message) {
        super(message);
    }
}
