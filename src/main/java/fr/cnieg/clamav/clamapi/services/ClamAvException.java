package fr.cnieg.clamav.clamapi.services;

public class ClamAvException extends Exception {

    private static final long serialVersionUID = -15467746982321555L;

    public ClamAvException(final String message) {
        super(message);
    }
}
