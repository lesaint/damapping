package fr.phan.damapping.processor.impl;

/**
* CheckError -
*
* @author: Sébastien Lesaint
*/
public class CheckError extends Exception {

    public CheckError(String message) {
        super(message);
    }

    public CheckError(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckError(Throwable cause) {
        super(cause);
    }

    public CheckError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
