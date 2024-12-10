package pt.mocktail.kafkareplay.service;

public interface EventHandler {

    /**
     * Receives a message from your events source with a known origin.
     *
     * @param event the event being handled
     */
    void handleEvent(Event event);

    /**
     * How the handler is qualified to allow extensibility.
     *
     * @return a unique name for beans registration
     */
    String getQualifier();
}
