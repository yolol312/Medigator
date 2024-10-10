// MemberEntity.java
package com.medigator.medigator.entity;

import com.medigator.medigator.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

// MemberEntity.java
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "member_table")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String memberId;

    @Column
    private String memberPassword;

    @Column
    private String memberName;

    @Column(unique = true)
    private String memberEmail;

    @Column
    private LocalDate memberBirth;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private MyPageEntity myPage;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<MedicineEntity> medicines;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<MyDiseaseEntity> myDisease;
}