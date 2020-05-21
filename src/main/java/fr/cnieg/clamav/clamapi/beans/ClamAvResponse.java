package fr.cnieg.clamav.clamapi.beans;

public class ClamAvResponse {

    boolean isInfected;

    String message;

    public ClamAvResponse(final boolean isInfected, final String message) {
        this.isInfected = isInfected;
        this.message = message;
    }

    public boolean isInfected() {
        return isInfected;
    }

    public String getMessage() {
        return message;
    }
}
