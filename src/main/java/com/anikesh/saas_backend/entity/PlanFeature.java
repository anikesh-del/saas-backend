package com.anikesh.saas_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plan_features")
@Getter
@Setter
@NoArgsConstructor
public class PlanFeature {

    @EmbeddedId
    private PlanFeatureId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("planId")
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Column(name = "feature_value", nullable = false)
    private String featureValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlanFeature)) return false;
        PlanFeature that = (PlanFeature) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
