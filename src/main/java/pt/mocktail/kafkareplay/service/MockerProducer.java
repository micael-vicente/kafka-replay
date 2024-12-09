package pt.mocktail.kafkareplay.service;

public interface MockerProducer {

    /**
     * Sends the mock message to given destination.
     *
     * @param destination a topic, queue, etc
     * @param message the mock message
     */
    void sendMessage(String destination, String message);
}
