package de.firecreeper82.exceptions.exceptions;

import de.firecreeper82.exceptions.CustomException;

public class MemberNotFoundException extends CustomException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
