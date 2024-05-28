package org.zerock.recipe.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "items")
public class Refrigerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "refrigerator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefrigeratorItem> items = new ArrayList<>();
}
