package com.medigator.medigator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MedicineInteraction_table")
public class MedicineInteractionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id")
    private MedicineEntity medicine;
    @Column(length = 1000)
    private String drugInteractionDanger;

    @Column(length = 1000)
    private String drugInteractionWarn;

    @Column(length = 1000)
    private String drugFoodInteraction;

    @Column(length = 1000)
    private String drugConditionInteraction;

}
