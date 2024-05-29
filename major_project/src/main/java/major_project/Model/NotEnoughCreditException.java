package major_project.Model;

/**
 * This is an custom exception for not enough credit
 */
public class NotEnoughCreditException extends Exception {
    public NotEnoughCreditException(String message) {
        super(message);
    }
}
