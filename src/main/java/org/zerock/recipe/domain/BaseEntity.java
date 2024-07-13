package org.zerock.recipe.domain;


import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@MappedSuperclass
@EntityListeners(value = { AuditingEntityListener.class })
@Getter
abstract class BaseEntity {

    @CreatedDate
    @Column(name = "regdate", updatable = false)
    private LocalDate regDate;

    @LastModifiedDate
    @Column(name ="moddate" )
    private LocalDate modDate;

    @CreatedDate
    @Column(name="regtime", updatable = false)
    private LocalTime regTime;

    @LastModifiedDate
    @Column(name = "modtime")
    private LocalTime modTime;

    @PrePersist
    public void prePersist() {
        this.regDate = LocalDate.now();
        this.regTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
        this.modDate = this.regDate;
        this.modTime = this.regTime;
    }

    @PreUpdate
    public void preUpdate() {
        this.modDate = LocalDate.now();
        this.modTime = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
    }


}
