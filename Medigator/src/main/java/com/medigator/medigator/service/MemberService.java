package com.medigator.medigator.service;

import com.medigator.medigator.dto.MemberDTO;
import com.medigator.medigator.entity.MemberEntity;
import com.medigator.medigator.entity.MyPageEntity;
import com.medigator.medigator.repository.MedicineRepository;
import com.medigator.medigator.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MedicineRepository medicineRepository;

    public void join(MemberDTO memberDTO) {
        MemberEntity memberEntity = toMemberEntity(memberDTO);
        MyPageEntity myPageEntity = new MyPageEntity(); // 새 마이페이지 생성

        myPageEntity.setMember(memberEntity); // 연결 설정
        myPageEntity.setName(memberDTO.getMemberName()); // 회원 이름 설정
        myPageEntity.setAge(Period.between(memberDTO.getMemberBirth(), LocalDate.now()).getYears()); // 나이 계산하여 설정

        memberEntity.setMyPage(myPageEntity); // 양방향 연결
        memberRepository.save(memberEntity);
    }

    public LoginResponse login(MemberDTO memberDTO) {
        Optional<MemberEntity> byMemberId = memberRepository.findByMemberId(memberDTO.getMemberId());
        if (byMemberId.isPresent()) {
            MemberEntity memberEntity = byMemberId.get();
            if (memberEntity.getMemberPassword().equals(memberDTO.getMemberPassword())) {
                MemberDTO dto = toMemberDTO(memberEntity);
                return new LoginResponse(true, "로그인 성공", dto);
            } else {
                return new LoginResponse(false, "비밀번호가 틀렸습니다.", null);
            }
        } else {
            return new LoginResponse(false, "존재하지 않는 아이디입니다.", null);
        }
    }

    private static MemberEntity toMemberEntity(MemberDTO memberDTO) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(memberDTO.getId());
        memberEntity.setMemberId(memberDTO.getMemberId());
        memberEntity.setMemberPassword(memberDTO.getMemberPassword());
        memberEntity.setMemberName(memberDTO.getMemberName());
        memberEntity.setMemberEmail(memberDTO.getMemberEmail());
        memberEntity.setMemberBirth(memberDTO.getMemberBirth());
        return memberEntity;
    }

    public static MemberDTO toMemberDTO(MemberEntity memberEntity) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setMemberId(memberEntity.getMemberId());
        memberDTO.setMemberEmail(memberEntity.getMemberEmail());
        memberDTO.setMemberPassword(memberEntity.getMemberPassword());
        memberDTO.setMemberName(memberEntity.getMemberName());
        memberDTO.setMemberBirth(memberEntity.getMemberBirth());
        return memberDTO;
    }

    public String idCheck(String memberId) {
        return memberRepository.findByMemberId(memberId).isPresent() ? "사용할 수 없는 ID입니다." : "사용 가능한 ID입니다.";
    }

    public String emailCheck(String memberEmail) {
        return memberRepository.findByMemberEmail(memberEmail).isPresent() ? "사용할 수 없는 이메일입니다." : "사용 가능한 이메일입니다.";
    }

    public Optional<MemberEntity> findMemberBymemberId(String memberId) {
        return memberRepository.findByMemberId(memberId);
    }
}
