package com.alerts;

/**
 * Base decorator for alerts.
 */
public abstract class AlertDecorator implements Alert {
    protected Alert wrappedAlert;

    /**
     * Creates a decorator around another alert.
     *
     * @param wrappedAlert the alert to wrap
     */
    public AlertDecorator(Alert wrappedAlert) {
        this.wrappedAlert = wrappedAlert;
    }

    @Override
    public String getPatientId() {
        return wrappedAlert.getPatientId();
    }

    @Override
    public String getCondition() {
        return wrappedAlert.getCondition();
    }

    @Override
    public long getTimestamp() {
        return wrappedAlert.getTimestamp();
    }

    @Override
    public String getMessage() {
        return wrappedAlert.getMessage();
    }
}
