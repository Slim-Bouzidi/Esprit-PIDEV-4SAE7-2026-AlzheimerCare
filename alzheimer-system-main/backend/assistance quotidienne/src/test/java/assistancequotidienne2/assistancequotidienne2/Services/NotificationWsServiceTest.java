package assistancequotidienne2.assistancequotidienne2.Services;

import assistancequotidienne2.assistancequotidienne2.DTOs.DoctorNotificationMessage;
import assistancequotidienne2.assistancequotidienne2.Entities.Notification;
import assistancequotidienne2.assistancequotidienne2.Entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NotificationWsServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationWsService notificationWsService;

    @Test
    void shouldSendDoctorNotificationPayloadViaWebSocketTopic() {
        User doctor = new User();
        doctor.setId(42L);

        Notification notification = new Notification();
        notification.setId(100L);
        notification.setDestinataire(doctor);
        notification.setType("FICHE_ENVOYEE");
        notification.setTitre("Titre");
        notification.setMessage("Corps");
        notification.setReferenceType("FICHE");
        notification.setReferenceId(7L);
        notification.setDateCreation(LocalDateTime.of(2026, 4, 10, 12, 0));

        notificationWsService.notifyDoctor(notification);

        verify(messagingTemplate).convertAndSend(
                eq("/topic/doctor-notifications"),
                any(DoctorNotificationMessage.class)
        );
    }

    @Test
    void shouldNotSendWhenNotificationOrDestinataireIsInvalid() {
        notificationWsService.notifyDoctor(null);

        Notification missingDest = new Notification();
        notificationWsService.notifyDoctor(missingDest);

        User noId = new User();
        Notification n = new Notification();
        n.setDestinataire(noId);
        notificationWsService.notifyDoctor(n);

        verifyNoInteractions(messagingTemplate);
    }
}
