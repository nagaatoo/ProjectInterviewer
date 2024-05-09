package ru.numbdev.interviewer.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "templates")
@EntityListeners(AuditingEntityListener.class)
public class TemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    @CreatedBy
    private String owner;

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
    private List<InterviewEntity> interviews;

}
