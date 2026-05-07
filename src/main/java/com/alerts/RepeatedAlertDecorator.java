package com.alerts;

/**
 * Adds repeated-check text to an alert message.
 */
public class RepeatedAlertDecorator extends AlertDecorator {

    /**
     * Creates a repeated-check decorator.
     *
     * @param wrappedAlert the alert to wrap
     */
    public RepeatedAlertDecorator(Alert wrappedAlert) {
        super(wrappedAlert);
    }

    @Override
    public String getMessage() {
        return "[REPEATED CHECK] " + wrappedAlert.getMessage();
    }
}
