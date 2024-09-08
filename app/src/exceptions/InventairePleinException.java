package exceptions;

public class InventairePleinException extends RuntimeException {
    public InventairePleinException(String message) {
        super(message);
    }
}
