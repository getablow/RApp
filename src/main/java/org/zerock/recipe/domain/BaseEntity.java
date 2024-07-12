package org.zerock.recipe.domain;


import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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


}
