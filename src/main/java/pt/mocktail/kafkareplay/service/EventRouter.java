package pt.mocktail.kafkareplay.service;

import org.springframework.stereotype.Service;
import pt.mocktail.kafkareplay.service.exception.HandlerNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventRouter {

    private final Map<String, EventHandler> handlers;

    /**
     * Handlers auto registration.
     *
     * @param handlers handler beans found
     */
    public EventRouter(List<EventHandler> handlers) {
        this.handlers = handlers.stream()
            .collect(Collectors.toMap(
                EventHandler::getQualifier,
                Function.identity()
            ));
    }

    /**
     * Routes the event to the correct handler.
     *
     * @param handler the qualifier of the handler to route to
     * @param event the event being routed
     */
    public void route(String handler, Event event) {
        Optional.ofNullable(handlers.get(handler))
            .ifPresentOrElse(
                eventHandler -> eventHandler.handleEvent(event),
                HandlerNotFoundException::new
            );
    }
}
