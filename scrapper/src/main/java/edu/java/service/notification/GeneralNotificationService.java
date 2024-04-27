package edu.java.service.notification;

import edu.java.dto.request.LinkUpdateRequest;
import edu.java.service.sender.UpdateSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeneralNotificationService {

    private final UpdateSender sender;

    public void sendUpdate(LinkUpdateRequest request) {
        sender.sendUpdate(request);
    }
}
