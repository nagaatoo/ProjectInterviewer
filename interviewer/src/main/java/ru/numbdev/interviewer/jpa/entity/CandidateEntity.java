package ru.numbdev.interviewer.jpa.entity;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import ru.numbdev.interviewer.enums.CandidateSolution;

@Getter
@Setter
@Entity
@Table(name = "candidates")
public class CandidateEntity extends AbstractAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String fio;
    private String description;
    private Boolean deleted;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id")
    private FileEntity file;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private CandidateSolution candidateSolution;

    @OneToMany(mappedBy = "candidate", fetch = FetchType.LAZY)
    private List<InterviewEntity> interviews;
}
