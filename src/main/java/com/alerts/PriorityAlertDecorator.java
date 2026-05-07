package com.alerts;

/**
 * Adds priority text to an alert message.
 */
public class PriorityAlertDecorator extends AlertDecorator {

    /**
     * Creates a priority decorator.
     *
     * @param wrappedAlert the alert to wrap
     */
    public PriorityAlertDecorator(Alert wrappedAlert) {
        super(wrappedAlert);
    }

    @Override
    public String getMessage() {
        return "[PRIORITY] " + wrappedAlert.getMessage();
    }
}
