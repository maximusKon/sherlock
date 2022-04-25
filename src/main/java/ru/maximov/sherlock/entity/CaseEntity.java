package ru.maximov.sherlock.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cases")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseEntity {

    @Id
    @SequenceGenerator(
        name = "cases_seq", sequenceName = "cases_seq", allocationSize = 1)
    @GeneratedValue(generator = "cases_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    private String caseId;

    private String description;

    private CaseStatus status;

    private LocalDateTime completedTime;

    private CaseResult result;

    private String resultComment;

}

