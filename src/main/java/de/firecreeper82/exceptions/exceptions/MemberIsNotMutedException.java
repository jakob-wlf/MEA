package de.firecreeper82.exceptions.exceptions;

import de.firecreeper82.exceptions.CustomException;

public class MemberIsNotMutedException extends CustomException {
    public MemberIsNotMutedException(String message) {
        super(message);
    }
}
