package assistancequotidienne2.assistancequotidienne2.Repositories;

import assistancequotidienne2.assistancequotidienne2.Entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireIdOrderByDateCreationDesc(Long destinataireId);
    List<Notification> findByDestinataireIdAndLuFalseOrderByDateCreationDesc(Long destinataireId);
    long countByDestinataireIdAndLuFalse(Long destinataireId);
    List<Notification> findByPatientIdOrderByDateCreationDesc(Long patientId);
}
