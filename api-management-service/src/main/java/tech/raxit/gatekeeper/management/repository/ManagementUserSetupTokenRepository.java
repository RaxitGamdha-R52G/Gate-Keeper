
package tech.raxit.gatekeeper.management.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.raxit.gatekeeper.management.entity.ManagementUserSetupToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ManagementUserSetupTokenRepository
        extends JpaRepository<ManagementUserSetupToken, Long> {

    Optional<ManagementUserSetupToken> findByTokenHash(String tokenHash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT token
            FROM ManagementUserSetupToken token
            WHERE token.tokenHash = :tokenHash
            """)
    Optional<ManagementUserSetupToken> findByTokenHashForUpdate(
            @Param("tokenHash") String tokenHash
    );

    @Query("""
            SELECT token
            FROM ManagementUserSetupToken token
            WHERE token.tokenHash = :tokenHash
              AND token.expiresAt > :now
              AND token.usedAt IS NULL
              AND token.revokedAt IS NULL
            """)
    Optional<ManagementUserSetupToken> findUsableToken(
            @Param("tokenHash") String tokenHash,
            @Param("now") LocalDateTime now
    );

    @Query("""
            SELECT token
            FROM ManagementUserSetupToken token
            WHERE token.managementUser.id = :userId
              AND token.usedAt IS NULL
              AND token.revokedAt IS NULL
            """)
    List<ManagementUserSetupToken> findPendingTokensByUserId(
            @Param("userId") Long userId
    );
}
