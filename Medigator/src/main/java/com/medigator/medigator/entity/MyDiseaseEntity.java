// MyPageEntity.java

package com.medigator.medigator.entity;

import com.medigator.medigator.dto.MyPageDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "mydisease_table")
public class MyDiseaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myDiseaseid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 데이터베이스에서 회원 ID를 외래 키로 사용
    private MemberEntity member;

    @Column
    private String disease;
}
