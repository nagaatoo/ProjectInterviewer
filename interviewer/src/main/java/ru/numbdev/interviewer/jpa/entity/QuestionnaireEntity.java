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
@Table(name = "questionnaires")
@EntityListeners(AuditingEntityListener.class)
public class QuestionnaireEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private Integer version;
    private String name;
    private String description;

    @CreatedBy
    private String author;

    @OneToMany(mappedBy = "questionnaire", fetch = FetchType.LAZY)
    private List<InterviewEntity> interviews;

    @OneToMany(mappedBy = "questionnaire", fetch = FetchType.LAZY)
    private List<BuilderItemEntity> items;
}
