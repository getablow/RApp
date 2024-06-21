package org.zerock.recipe.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "refrigerator")
@EntityListeners(value = { AuditingEntityListener.class })
public class RefrigeratorItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private String quantity;
    private LocalDate expirationDate;

    @CreatedDate
    @Column(name = "added_date", updatable = false)
    private LocalDate addedDate;

    @ManyToOne
    @JoinColumn(name = "refrigerator_id")
    private Refrigerator refrigerator;


}
