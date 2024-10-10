package com.medigator.medigator.controller;

import com.medigator.medigator.dto.MedicineInteractionDTO;
import com.medigator.medigator.dto.MaterialNameDTO;
import com.medigator.medigator.entity.MemberEntity;
import com.medigator.medigator.repository.MemberRepository;
import com.medigator.medigator.service.MedicineInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/member/mypage")
public class InteractionController {

    @Autowired
    private MedicineInteractionService medicineInteractionService;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/medicines2")
    public List<MaterialNameDTO> getMedicines(HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        System.out.println(loginId);
        Optional<MemberEntity> memberId = memberRepository.findByMemberId(loginId);
        return medicineInteractionService.getMedicinesByMemberId(memberId.get().getId());
    }

    @GetMapping("/interaction")
    public List<MedicineInteractionDTO> getInteractions(@RequestParam Long medicineId, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");
        Long memberId = getMemberIdByLoginId(loginId);
        return medicineInteractionService.getInteractionsByMedicineId(medicineId, memberId);
    }

    @GetMapping("/interaction2")
    public List<MedicineInteractionDTO> getMedicineList(@RequestParam String memberId, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");

        Long memberId2 = memberRepository.findByMemberId(memberId).get().getId();
        System.out.println("memberId2 : ");
        System.out.println(memberId2);

        return medicineInteractionService.getInteractionsByMemberId(memberId2);
    }

    @GetMapping("/medName")
    public String getMedicineName(@RequestParam Long medicineId, HttpSession session) {
        String loginId = (String) session.getAttribute("loginId");

        return medicineInteractionService.getmedicineNameBymedicineId(medicineId);
    }

    public Long getMemberIdByLoginId(String loginId) {
        MemberEntity member = memberRepository.findByMemberId(loginId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return member.getId();
    }


}
