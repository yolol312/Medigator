package com.medigator.medigator.service;

import com.medigator.medigator.dto.MaterialNameDTO;
import com.medigator.medigator.dto.MedicineInteractionDTO;
import com.medigator.medigator.entity.MaterialNameEntity;
import com.medigator.medigator.entity.MedicineInteractionEntity;
import com.medigator.medigator.entity.MedicineEntity;
import com.medigator.medigator.repository.MemberRepository;
import com.medigator.medigator.repository.MedicineInteractionRepository;
import com.medigator.medigator.repository.MedicineRepository;
import com.medigator.medigator.repository.MaterialNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicineInteractionService {

    @Autowired
    private MedicineInteractionRepository medicineInteractionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Autowired
    private MaterialNameRepository materialNameRepository;

    public List<MaterialNameDTO> getMedicinesByMemberId(Long memberId) {
        List<MedicineEntity> medicines = medicineRepository.findByMemberId(memberId);
        return medicines.stream().map(medicine -> {
            Optional<MaterialNameEntity> materialNameOpt = materialNameRepository.findByMedicineId(medicine.getId());
            String materialName = materialNameOpt.map(MaterialNameEntity::getMaterialName).orElse("");
            return new MaterialNameDTO(
                    medicine.getId(),
                    medicine.getMember().getId(),
                    medicine.getItemName(),
                    materialName
            );
        }).collect(Collectors.toList());
    }

    public List<MedicineInteractionDTO> getInteractionsByMedicineId(Long medicineId, Long memberId) {
        List<MedicineInteractionEntity> interactionEntities = medicineInteractionRepository.findByMedicineId(medicineId);
        return interactionEntities.stream()
                .map(entity -> new MedicineInteractionDTO(
                        entity.getInteractionId(),
                        memberId,
                        entity.getMedicine().getId(),
                        entity.getDrugInteractionDanger(),
                        entity.getDrugInteractionWarn(),
                        entity.getDrugFoodInteraction(),
                        entity.getDrugConditionInteraction()
                ))
                .collect(Collectors.toList());
    }

    public List<MedicineInteractionDTO> getInteractionsByMemberId(Long memberId2) {
        List<MedicineInteractionEntity> interactionEntities = medicineInteractionRepository.findByMemberId(memberId2);
        return interactionEntities.stream()
                .map(entity -> new MedicineInteractionDTO(
                        entity.getInteractionId(),
                        memberId2,
                        entity.getMedicine().getId(),
                        entity.getDrugInteractionDanger(),
                        entity.getDrugInteractionWarn(),
                        entity.getDrugFoodInteraction(),
                        entity.getDrugConditionInteraction()
                ))
                .collect(Collectors.toList());
    }

    public String getmedicineNameBymedicineId(Long medicineId) {
        var medicineName = medicineRepository.findById(medicineId).get().getItemName();
        return medicineName;
    }
}
