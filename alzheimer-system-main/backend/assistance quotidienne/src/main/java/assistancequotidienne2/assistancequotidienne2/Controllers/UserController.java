package assistancequotidienne2.assistancequotidienne2.Controllers;

import assistancequotidienne2.assistancequotidienne2.Entities.Role;
import assistancequotidienne2.assistancequotidienne2.Entities.User;
import assistancequotidienne2.assistancequotidienne2.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // CREATE
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        if (user.getActif() == null) {
            user.setActif(true);
        }
        return ResponseEntity.ok(userRepository.save(user));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
        return ResponseEntity.ok(user);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
        
        existing.setNom(user.getNom());
        existing.setEmail(user.getEmail());
        existing.setTelephone(user.getTelephone());
        existing.setRole(user.getRole());
        existing.setActif(user.getActif());
        
        if (user.getMot_de_passe() != null && !user.getMot_de_passe().isEmpty()) {
            existing.setMot_de_passe(user.getMot_de_passe());
        }
        
        return ResponseEntity.ok(userRepository.save(existing));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET BY ROLE
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userRepository.findByRole(role));
    }

    // GET ACTIVE USERS
    @GetMapping("/actifs")
    public ResponseEntity<List<User>> getActiveUsers() {
        return ResponseEntity.ok(userRepository.findByActifTrue());
    }

    // LOGIN SIMPLE (sans JWT pour l'instant)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Email ou mot de passe incorrect"));
        }
        if (!user.getMot_de_passe().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("error", "Email ou mot de passe incorrect"));
        }
        if (!user.getActif()) {
            return ResponseEntity.status(403).body(Map.of("error", "Compte désactivé"));
        }

        // Retourner les infos de l'utilisateur (sans mot de passe)
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("nom", user.getNom());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("telephone", user.getTelephone());
        response.put("token", "simple-token-" + user.getId()); // Token simplifié
        
        return ResponseEntity.ok(response);
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Vérifier si l'email existe déjà
        User existing = userRepository.findByEmail(user.getEmail());
        if (existing != null) {
            return ResponseEntity.status(409).body(Map.of("error", "Cet email est déjà utilisé"));
        }
        
        if (user.getActif() == null) {
            user.setActif(true);
        }
        
        User saved = userRepository.save(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", saved.getId());
        response.put("nom", saved.getNom());
        response.put("email", saved.getEmail());
        response.put("role", saved.getRole().name());
        
        return ResponseEntity.ok(response);
    }
}
