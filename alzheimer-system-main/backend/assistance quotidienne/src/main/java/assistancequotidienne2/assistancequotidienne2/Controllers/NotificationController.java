package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Entities.Notification;
import assistancequotidienne2.assistancequotidienne2.Repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    // GET all notifications for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationRepository.findByDestinataireIdOrderByDateCreationDesc(userId));
    }

    // GET unread notifications for a user
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationRepository.findByDestinataireIdAndLuFalseOrderByDateCreationDesc(userId));
    }

    // GET unread count
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationRepository.countByDestinataireIdAndLuFalse(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // MARK as read
    @PatchMapping("/{id}/lu")
    public ResponseEntity<Notification> marquerLu(@PathVariable Long id) {
        Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        notif.marquerLu();
        return ResponseEntity.ok(notificationRepository.save(notif));
    }

    // MARK ALL as read for a user
    @PatchMapping("/user/{userId}/lu-all")
    public ResponseEntity<Void> marquerToutLu(@PathVariable Long userId) {
        List<Notification> unread = notificationRepository.findByDestinataireIdAndLuFalseOrderByDateCreationDesc(userId);
        unread.forEach(Notification::marquerLu);
        notificationRepository.saveAll(unread);
        return ResponseEntity.ok().build();
    }

    // CREATE (used internally, but exposed for flexibility)
    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody Notification notification) {
        return ResponseEntity.ok(notificationRepository.save(notification));
    }

    // GET all (admin)
    @GetMapping
    public ResponseEntity<List<Notification>> getAll() {
        return ResponseEntity.ok(notificationRepository.findAll());
    }
}
