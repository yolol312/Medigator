// MyPageEntity.java

package com.medigator.medigator.entity;

import com.medigator.medigator.dto.MyPageDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "mypage_table")
public class MyPageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long myPageid;

    @OneToOne
    @JoinColumn(name = "member_id") // 데이터베이스에서 회원 ID를 외래 키로 사용
    private MemberEntity member;

    @Column(unique = true)
    private String name;

    @Column
    private String sex;

    @Column
    private int age;
}
