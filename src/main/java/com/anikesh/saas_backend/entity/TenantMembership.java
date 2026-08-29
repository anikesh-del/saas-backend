// TenantMembership.java
package com.anikesh.saas_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tenant_memberships")
@Getter
@Setter
@NoArgsConstructor
public class TenantMembership {

    @EmbeddedId
    private TenantMembershipId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tenantId")           
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")             
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "joined_at", nullable = false)
    private OffsetDateTime joinedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TenantMembership)) return false;
        TenantMembership that = (TenantMembership) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}