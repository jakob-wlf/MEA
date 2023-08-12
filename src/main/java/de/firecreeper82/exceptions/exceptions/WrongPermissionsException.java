package de.firecreeper82.exceptions.exceptions;

import de.firecreeper82.exceptions.CustomException;

public class WrongPermissionsException extends CustomException {

    public WrongPermissionsException(String message) {
        super(message);
    }
}
