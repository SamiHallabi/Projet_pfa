package websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;

@CrossOrigin
@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/seat-update/{showId}")
    public void seatUpdate(@DestinationVariable Long showId, Map<String, Object> seatUpdate) {
        // Simply broadcast the seat update to all clients subscribed to this show's seat updates
        messagingTemplate.convertAndSend("/topic/seats/" + showId, seatUpdate);
    }
}