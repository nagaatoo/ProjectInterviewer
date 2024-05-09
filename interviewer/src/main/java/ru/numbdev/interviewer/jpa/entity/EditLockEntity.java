package ru.numbdev.interviewer.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "edit_lock")
public class EditLockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDateTime startEdit;
    private LocalDateTime unlockEdit;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_id")
    private InterviewEntity interview;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "template_id")
    private TemplateEntity template;
}
