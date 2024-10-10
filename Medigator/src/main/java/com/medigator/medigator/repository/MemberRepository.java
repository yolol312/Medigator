// MemberRepository.java
package com.medigator.medigator.repository;

import com.medigator.medigator.dto.MemberDTO;
import com.medigator.medigator.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByMemberId(String memberId);
    Optional<MemberEntity> findByMemberEmail(String memberEmail); // 이메일로 멤버 조회 메소드


}
