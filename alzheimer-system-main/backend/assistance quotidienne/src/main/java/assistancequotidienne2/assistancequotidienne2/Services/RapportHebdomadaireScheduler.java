package assistancequotidienne2.assistancequotidienne2.Services;

import assistancequotidienne2.assistancequotidienne2.Entities.*;
import assistancequotidienne2.assistancequotidienne2.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service de génération automatique des rapports hebdomadaires
 * Basé sur les fiches de transmission de la semaine
 * 
 * Planification: Tous les lundis à 09:00
 */
@Service
public class RapportHebdomadaireScheduler {

    @Autowired
    private RapportHebdomadaireService rapportHebdoService;

    /**
     * Consolidation automatique journalière
     * Cron: Chaque soir à 23:00
     */
    @Scheduled(cron = "0 0 23 * * *")
    @Transactional
    public void genererRapportsHebdomadaires() {
        System.out.println("🔄 [SCHEDULER] Consolidation automatique - " + LocalDateTime.now());

        LocalDate aujourdhui = LocalDate.now();
        LocalDate debut = aujourdhui.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate fin = debut.plusDays(6);

        // Récupérer les patients ayant des fiches cette semaine
        List<FicheTransmission> fichesSemaine = ficheRepository.findByStatutAndDateFicheBetween("envoye", debut, fin);
        
        Map<Long, List<FicheTransmission>> byPatient = fichesSemaine.stream()
                .filter(f -> f.getPatient() != null)
                .collect(Collectors.groupingBy(f -> f.getPatient().getId()));

        boolean estDimancheRange = (aujourdhui.getDayOfWeek() == DayOfWeek.SUNDAY);

        for (Long patientId : byPatient.keySet()) {
            try {
                rapportHebdoService.consolider(patientId, debut, fin, estDimancheRange);
            } catch (Exception e) {
                System.err.println("❌ Erreur consolidation patient " + patientId + ": " + e.getMessage());
            }
        }
    }
}
