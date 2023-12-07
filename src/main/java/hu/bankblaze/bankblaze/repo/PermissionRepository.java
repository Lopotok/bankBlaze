package hu.bankblaze.bankblaze.repo;

import hu.bankblaze.bankblaze.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Permission findByEmployeeId(Long loggedInUserId);

    int countByForRetailTrue();

    int countByForCorporateTrue();

    int countByForTellerTrue();

    int countByForPremiumTrue();

    @Query("SELECT MAX(p.id) FROM Permission p")
    Long findMaxId();


}