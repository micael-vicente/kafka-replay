package pt.mocktail.kafkareplay.service;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Event {
    private String origin;
    private String message;
    private boolean envelopedMessage;
    private String fieldContainingMessage;
}
