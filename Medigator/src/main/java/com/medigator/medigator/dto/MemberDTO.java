// MemberDTO.java

package com.medigator.medigator.dto;

import com.medigator.medigator.entity.MedicineEntity;
import com.medigator.medigator.entity.MemberEntity;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
// 회원 정보에 필요한 정보를 필드로 정의
public class MemberDTO {
    private Long id;
    private String memberId;
    private String memberPassword;
    private String memberName;
    private String memberEmail;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate memberBirth;
}
