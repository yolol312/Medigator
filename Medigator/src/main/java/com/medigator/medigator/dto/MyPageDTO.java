// MyPageDTO.java

package com.medigator.medigator.dto;

import com.medigator.medigator.entity.MemberEntity;
import com.medigator.medigator.entity.MyPageEntity;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
// 회원 정보에 필요한 정보를 필드로 정의
public class MyPageDTO {
    private Long myPageDTOid;
    private String name;
    private String sex;
    private int age;
}
