package org.scooterrental.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.scooterrental.model.enums.RentalPointType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rental_points")
public class RentalPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_point_id")
    private Long rentalPointId;

    @Column(name = "rental_point_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RentalPointType rentalPointType;

    @Column(name = "location", nullable = false)
    private String location;

    @ManyToOne
    @JoinColumn(name = "parent_point_id")
    private RentalPoint parentPoint;

    @OneToMany(mappedBy = "parentPoint")
    private List<RentalPoint> childPoints = new ArrayList<>();

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
