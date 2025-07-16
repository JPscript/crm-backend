package org.ediae.tfm.crmapi.exception;

public class GeneralExceptionRegister extends GeneralException {
    public GeneralExceptionRegister(int errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage);
        initCause(cause);
    }
}
