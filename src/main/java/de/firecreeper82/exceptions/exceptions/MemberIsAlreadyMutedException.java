package de.firecreeper82.exceptions.exceptions;

import de.firecreeper82.exceptions.CustomException;

public class MemberIsAlreadyMutedException extends CustomException {
    public MemberIsAlreadyMutedException(String message) {
        super(message);
    }
}
