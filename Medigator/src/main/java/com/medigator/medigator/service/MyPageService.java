// MyPageService.java
package com.medigator.medigator.service;

import com.medigator.medigator.dto.*;
import com.medigator.medigator.entity.*;
import com.medigator.medigator.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

// spring bean으로 등록
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    @Autowired
    private final MedicineRepository medicineRepository;
    private final MemberRepository memberRepository;
    private final MyPageRepository myPageRepository;
    private final MyDiseaseRepository myDiseaseRepository;
    private final MaterialNameRepository materialNameRepository;  // 추가
    private final MedicineInteractionRepository medicineInteractionRepository;

    @Transactional
    public boolean updateMyPageInfo(MyPageDTO myPageDTO, String memberId) {
        Optional<MemberEntity> memberEntity = memberRepository.findByMemberId(memberId);
        if (memberEntity.isPresent()) {
            MyPageEntity myPage = memberEntity.get().getMyPage();
            if (myPage != null) {
                myPage.setSex(myPageDTO.getSex());
                myPage.setAge(myPageDTO.getAge());
                myPageRepository.save(myPage);
                return true;
            } else {
                // myPage가 null인 경우 처리
                return false;
            }
        }
        return false;
    }

    public void addMedicine(MedicineDTO medicineDTO, String memberId) {
        Optional<MemberEntity> memberEntity = memberRepository.findByMemberId(memberId);
        if (memberEntity.isPresent()) {
            MedicineEntity medicine = toMedicineEntity(medicineDTO);
            medicine.setMember(memberEntity.get()); // 연결된 회원 설정
            medicineRepository.save(medicine);
        }
    }

    public MyPageDTO getDetailsByMemberId(String memberId) {
        Optional<MemberEntity> memberEntity = memberRepository.findByMemberId(memberId);
        if (memberEntity.isPresent() && memberEntity.get().getMyPage() != null) {
            MyPageEntity myPage = memberEntity.get().getMyPage();
            MyPageDTO myPageDTO = new MyPageDTO();
            myPageDTO.setMyPageDTOid(myPage.getMyPageid());
            myPageDTO.setName(myPage.getName());
            myPageDTO.setSex(myPage.getSex());
            myPageDTO.setAge(myPage.getAge());
            return myPageDTO;
        }
        return new MyPageDTO(); // 기본값 반환
    }

    public List<MedicineDTO> getMedicinesByMemberId(String memberId) {
        Optional<MemberEntity> memberEntity = memberRepository.findByMemberId(memberId);
        if (memberEntity.isPresent()) {
            Set<MedicineEntity> medicines = memberEntity.get().getMedicines();
            return medicines.stream().map(medicine -> {
                MedicineDTO dto = new MedicineDTO();
                dto.setEntpName(medicine.getEntpName());
                dto.setItemName(medicine.getItemName());
                dto.setEfcyQesitm(medicine.getEfcyQesitm());
                dto.setAtpnQesitm(medicine.getAtpnQesitm());
                dto.setUseMethodQesitm(medicine.getUseMethodQesitm());
                return dto;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static MedicineEntity toMedicineEntity(MedicineDTO medicineDTO) {
        MedicineEntity medicineEntity = new MedicineEntity();
        medicineEntity.setEntpName(medicineDTO.getEntpName());
        medicineEntity.setItemName(medicineDTO.getItemName());
        medicineEntity.setEfcyQesitm(medicineDTO.getEfcyQesitm());
        medicineEntity.setAtpnQesitm(medicineDTO.getAtpnQesitm());
        medicineEntity.setUseMethodQesitm(medicineDTO.getUseMethodQesitm());
        medicineEntity.setIntrcQesitm(medicineDTO.getIntrcQesitm());
        return medicineEntity;
    }

    public List<MedicineDTO> getMedicinesByItemName(String itemName) {
        List<MedicineEntity> medicines = medicineRepository.findAllByItemName(itemName);
        if (medicines.isEmpty()) {
            System.out.println("비어있음");
            System.out.println(itemName);
        } else {
            System.out.println("비어있지않음");
            System.out.println(medicines.size());
        }
        return medicines.stream().map(this::toMedicineDTO).collect(Collectors.toList());
    }

    private MedicineDTO toMedicineDTO(MedicineEntity entity) {
        MedicineDTO dto = new MedicineDTO();
        dto.setEntpName(entity.getEntpName());
        dto.setItemName(entity.getItemName());
        dto.setEfcyQesitm(entity.getEfcyQesitm());
        dto.setAtpnQesitm(entity.getAtpnQesitm());
        dto.setUseMethodQesitm(entity.getUseMethodQesitm());
        dto.setIntrcQesitm(entity.getIntrcQesitm());
        return dto;
    }

    @Transactional
    public boolean updateMyDiseaseInfo(String myDisease, String memberId) {
        Optional<MemberEntity> memberEntityOpt = memberRepository.findByMemberId(memberId);

        if (memberEntityOpt.isPresent()) {
            MemberEntity memberEntity = memberEntityOpt.get();
            Set<MyDiseaseEntity> existingDiseases = memberEntity.getMyDisease();

            // 새로운 질병이 이미 존재하는지 확인
            boolean diseaseExists = existingDiseases.stream()
                    .anyMatch(disease -> disease.getDisease().equals(myDisease));

            if (!diseaseExists) {
                // 새로운 질병 추가
                MyDiseaseEntity newDisease = new MyDiseaseEntity();
                newDisease.setDisease(myDisease);
                newDisease.setMember(memberEntity);
                myDiseaseRepository.save(newDisease);
                return true;
            } else {
                // 질병이 이미 존재할 경우
                System.out.println("질병이 이미 존재합니다.");
            }
        }

        return false;
    }

    public List<String> getDiseasesByMemberId(String memberId) {
        Optional<MemberEntity> memberEntity = memberRepository.findByMemberId(memberId);
        if (memberEntity.isPresent()) {
            Set<MyDiseaseEntity> diseasess = memberEntity.get().getMyDisease();
            return diseasess.stream().map(diseases -> {
                String str = diseases.getDisease();
                return str;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public List<CompareDTO> getDiseasesCompare(List<String> diseases, List<MedicineDTO> medicines) {
        List<CompareDTO> matchingDiseases = new ArrayList<>();
        for (String disease : diseases) {
            List<MedicineDTO> matchingMedicines = new ArrayList<>();
            int all_count = 0;
            int matching_count = 0;
            List<Boolean> types = new ArrayList<>();
            for (MedicineDTO medicine : medicines) {
                if (medicine.getAtpnQesitm() != null && medicine.getAtpnQesitm().contains(disease) && !medicine.getEfcyQesitm().contains(disease)) {
                    matchingMedicines.add(medicine);
                    matching_count++;
                    all_count++;
                    types.add(false);
                }
                else if (medicine.getAtpnQesitm() != null && medicine.getEfcyQesitm().contains(disease) && !medicine.getAtpnQesitm().contains(disease)){
                    matchingMedicines.add(medicine);
                    all_count++;
                    types.add(true);
                }
            }
            matchingDiseases.add(new CompareDTO(disease, matchingMedicines, matching_count, all_count, types));
        }
        return matchingDiseases;
    }


    @Transactional
    public boolean saveMaterialName(String materialName, String itemName) {
        try {
            // ITEM_NAME을 이용하여 MedicineEntity를 찾기
            List<MedicineEntity> medicineEntities = medicineRepository.findAllByItemName(itemName);
            if (!medicineEntities.isEmpty()) {
                MedicineEntity medicineEntity = medicineEntities.get(0); // 첫 번째 항목 선택

                // MATERIAL_NAME을 DB에 저장하는 로직 구현
                MaterialNameEntity materialNameEntity = new MaterialNameEntity();
                materialNameEntity.setMedicine(medicineEntity); // MedicineEntity 설정
                materialNameEntity.setItemName(itemName);
                materialNameEntity.setMaterialName(materialName);

                materialNameRepository.save(materialNameEntity); // JPA 사용 시
                return true;
            } else {
                System.err.println("MedicineEntity를 찾지 못했습니다: " + itemName);
                return false; // MedicineEntity를 찾지 못한 경우
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<MedicineInteractionDTO> getMedicineInteractions(String itemName) {
        List<MedicineInteractionEntity> interactions = medicineInteractionRepository.findByMedicineItemName(itemName);
        return interactions.stream().map(this::toMedicineInteractionDTO).collect(Collectors.toList());
    }


    private MedicineInteractionDTO toMedicineInteractionDTO(MedicineInteractionEntity entity) {
        MedicineInteractionDTO dto = new MedicineInteractionDTO();
        dto.setDrugConditionInteraction(entity.getDrugConditionInteraction());
        dto.setDrugFoodInteraction(entity.getDrugFoodInteraction());
        dto.setDrugInteractionDanger(entity.getDrugInteractionDanger());
        dto.setDrugInteractionWarn(entity.getDrugInteractionWarn());
        return dto;
    }

    @Transactional
    public boolean deleteMyDiseaseInfo(String myDisease, String memberId) {
        Optional<MemberEntity> memberEntityOpt = memberRepository.findByMemberId(memberId);
        if (memberEntityOpt.isPresent()) {
            MemberEntity memberEntity = memberEntityOpt.get();

            String myDisease_clean = myDisease.replace("\"", "");  // 큰따옴표 모두 제거

            Optional<MyDiseaseEntity> diseaseEntityOpt = myDiseaseRepository.findByDiseaseAndMember(myDisease_clean, memberEntity);

            if (diseaseEntityOpt.isPresent()) {
                MyDiseaseEntity diseaseEntity = diseaseEntityOpt.get();

                // 회원과 약품의 연관 관계를 확인합니다
                if (diseaseEntity.getMember().equals(memberEntity)) {
                    System.out.println(diseaseEntity.getDisease());
                    System.out.println(diseaseEntity.getMyDiseaseid());

                    // MemberEntity의 MedicineEntity 리스트에서 해당 MedicineEntity 삭제
                    memberEntity.getMyDisease().remove(diseaseEntity);

                    myDiseaseRepository.delete(diseaseEntity);
                    return true;
                } else {
                    System.out.println(3);
                    return false;
                }
            } else {
                System.out.println(2);
                return false;
            }
        } else {
            System.out.println(1);
            return false;
        }
    }
}
