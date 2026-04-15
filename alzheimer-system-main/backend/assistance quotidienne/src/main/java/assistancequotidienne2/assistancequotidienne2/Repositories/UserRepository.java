package assistancequotidienne2.assistancequotidienne2.Repositories;

import assistancequotidienne2.assistancequotidienne2.Entities.User;
import assistancequotidienne2.assistancequotidienne2.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Trouver par email
    User findByEmail(String email);
    
    // Trouver par rôle
    List<User> findByRole(Role role);
    
    // Trouver les utilisateurs actifs
    List<User> findByActifTrue();
}
