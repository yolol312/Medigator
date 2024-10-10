package com.medigator.medigator.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medigator.medigator.dto.CompareMedicineDTO;
import com.medigator.medigator.dto.MedicineDTO;
import com.medigator.medigator.dto.MedicineInteractionDTO;
import com.medigator.medigator.entity.MaterialNameEntity;
import com.medigator.medigator.entity.MedicineEntity;
import com.medigator.medigator.entity.MedicineInteractionEntity;
import com.medigator.medigator.entity.MemberEntity;
import com.medigator.medigator.repository.MaterialNameRepository;
import com.medigator.medigator.repository.MedicineInteractionRepository;
import com.medigator.medigator.repository.MedicineRepository;
import com.medigator.medigator.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// MedicineService.java 수정된 부분
@Service
public class MedicineService {

    @Autowired
    private MedicineInteractionRepository medicineInteractionRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MedicineRepository medicineRepository;
    @Autowired
    private MaterialNameRepository materialNameRepository;
    @Autowired
    private MemberService memberService;

    // summary 파라미터를 추가하여 ApiController에서 받은 데이터를 직접 사용
    public void saveInteraction(Map<String, Object> summary, String memberId, String itemName) throws Exception {
        // 회원 정보 조회
        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        // 약 정보 조회, itemName으로 조회하도록 변경
        MedicineEntity medicine = medicineRepository.findFirstByItemName(itemName)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        // 응답에서 정보 파싱 (summary에서 직접 추출)
        String responseContent = extractContentFromResponse(summary);
        MedicineInteractionEntity interaction = new MedicineInteractionEntity();
        interaction.setMember(member);
        interaction.setMedicine(medicine);
        System.out.println("responseContent : " + responseContent);

        interaction.setDrugInteractionDanger(extractCategoryData(responseContent, "경고 :"));
        interaction.setDrugInteractionWarn(extractCategoryData(responseContent, "주의 :"));
        interaction.setDrugFoodInteraction(extractCategoryData(responseContent, "음식 :"));
        interaction.setDrugConditionInteraction(extractCategoryData(responseContent, "질병 :"));


        System.out.println("Drug Interaction Danger: " + interaction.getDrugInteractionDanger());
        System.out.println("Drug Interaction Warn: " + interaction.getDrugInteractionWarn());
        System.out.println("Drug Food Interaction: " + interaction.getDrugFoodInteraction());
        System.out.println("Drug Condition Interaction: " + interaction.getDrugConditionInteraction());

        // 데이터베이스에 저장
        medicineInteractionRepository.save(interaction);
    }

    private String extractContentFromResponse(Map<String, Object> response) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty()) {
            System.out.println("No choices found in response");
            return null;
        }

        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        if (message == null) {
            System.out.println("No message found in response");
            return null;
        }

        String content = (String) message.get("content");
        if (content == null) {
            System.out.println("Invalid content found in response");
            return null;
        }

        return content;
    }

    private String extractCategoryData(String text, String categoryPrefix) {
        Pattern pattern = Pattern.compile(Pattern.quote(categoryPrefix) + "([^\\n]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null; // 데이터가 없으면 null 반환
    }

    public List<MedicineEntity> getMedicinesByMemberId(Long memberId) {
        return medicineRepository.findByMemberId(memberId);
    }

    public List<MedicineInteractionEntity> getInteractionsByMemberId(Long memberId) {
        return medicineInteractionRepository.findByMemberId(memberId);
    }

    public List<MedicineInteractionDTO> getMedicineInteractions(String memberId) {
        MemberEntity member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<MedicineEntity> medicines = medicineRepository.findByMember(member);

        return medicines.stream().flatMap(medicine -> {
            List<MedicineInteractionEntity> interactions = medicineInteractionRepository.findByMedicine(medicine);
            return interactions.stream().map(interaction -> new MedicineInteractionDTO(
                    interaction.getInteractionId(),
                    interaction.getMember().getId(),
                    interaction.getMedicine().getId(),
                    interaction.getDrugInteractionDanger(),
                    interaction.getDrugInteractionWarn(),
                    interaction.getDrugFoodInteraction(),
                    interaction.getDrugConditionInteraction()
            ));
        }).collect(Collectors.toList());
    }

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public boolean deleteMedicineByItemNameAndSessionMemberId(HttpSession session, String itemName) {
        try {
            // 세션에서 로그인 ID 가져오기
            String loginId = (String) session.getAttribute("loginId");
            System.out.println("loginId: " + loginId);

            // 로그인 ID로 회원 정보 조회
            Optional<MemberEntity> member = memberService.findMemberBymemberId(loginId);

            if (!member.isPresent()) {
                System.out.println("Member not found with loginId: " + loginId);
                return false;
            }

            Long memberId = member.get().getId();
            System.out.println("memberId: " + memberId);

            // 약품명과 회원 ID를 기반으로 약품 정보 가져오기
            MedicineEntity medicine = medicineRepository.findByItemNameAndMemberId(itemName, memberId);
            if (medicine == null) {
                System.out.println("No medicine found with itemName: " + itemName + " and memberId: " + memberId);
                return false;
            }

            Long medicineId = medicine.getId();
            System.out.println("medicineId: " + medicineId);
            System.out.println(medicine);

            // 해당 medicineId에 연결된 material_name 테이블 정보 삭제
            materialNameRepository.deleteByMedicineId(medicineId);

            // 해당 medicineId에 연결된 medicine_interaction_table 테이블 정보 삭제
            medicineInteractionRepository.deleteByMedicineId(medicineId);

            // 엔티티 매니저를 사용하여 수동으로 플러시
            entityManager.flush();

            MedicineEntity medicine2 = medicineRepository.findByItemNameAndMemberId(itemName, memberId);

            // 약 삭제 전에 디버그 로그 추가
            System.out.println("Deleting medicine with ID: " + medicineId);

            // 약 삭제
            medicineRepository.delete(medicine2);

            // 삭제 후 확인 로그
            System.out.println("Deleted medicine with ID: " + medicineId);

            // 엔티티 매니저를 사용하여 수동으로 플러시
            entityManager.flush();

            // 삭제 후 medicine 테이블에 해당 아이템이 남아있는지 확인
            MedicineEntity deletedMedicine = medicineRepository.findByItemNameAndMemberId(itemName, memberId);
            if (deletedMedicine == null) {
                System.out.println("Medicine successfully deleted from the database.");
            } else {
                System.out.println("Failed to delete medicine from the database. Medicine still exists.");
            }

            return true; // 삭제 성공
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 삭제 실패
        }
    }
}
