package tech.raxit.gatekeeper.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(
        name = "management_user_setup_tokens",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_management_user_setup_tokens_hash",
                        columnNames = "token_hash"
                )
        },
        indexes = {
                @Index(
                        name = "idx_management_user_setup_tokens_user",
                        columnList = "management_user_id"
                ),
                @Index(
                        name = "idx_management_user_setup_tokens_expiry",
                        columnList = "expires_at"
                )
        }
)
public class ManagementUserSetupToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "management_user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_management_user_setup_tokens_user"
            )
    )
    private ManagementUser managementUser;

    @Column(name = "token_hash",nullable = false, length = 64, unique = true, updatable = false)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ManagementUserSetupToken(
            ManagementUser managementUser,
            String tokenHash,
            LocalDateTime expiresAt,
            LocalDateTime createdAt
    ) {
        this.managementUser = managementUser;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public boolean isExpiredAt(LocalDateTime now) {
        return !expiresAt.isAfter(now);
    }

    public boolean isUsed() {
        return usedAt != null;
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isUsableAt(LocalDateTime now) {
        return !isExpiredAt(now) && !isUsed() && !isRevoked();
    }

    public void markUsed(LocalDateTime now) {
        if (!isUsableAt(now)) {
            throw new IllegalStateException(
                    "Setup token is expired, used, or revoked"
            );
        }

        this.usedAt = now;
    }

    public void revoke(LocalDateTime now) {
        if (isUsed()) {
            throw new IllegalStateException(
                    "A used setup token cannot be revoked"
            );
        }

        if (!isRevoked()) {
            this.revokedAt = now;
        }
    }
}
