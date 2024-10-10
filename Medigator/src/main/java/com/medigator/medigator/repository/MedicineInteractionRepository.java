package com.medigator.medigator.repository;

import com.medigator.medigator.entity.MedicineEntity;
import com.medigator.medigator.entity.MedicineInteractionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineInteractionRepository extends JpaRepository<MedicineInteractionEntity, Long> {
    List<MedicineInteractionEntity> findByMemberId(Long memberId);
    List<MedicineInteractionEntity> findByMedicine(MedicineEntity medicine);
    List<MedicineInteractionEntity> findByMedicineItemName(String itemName);

    List<MedicineInteractionEntity> findByMedicineId(Long medicineId);

    void deleteByMedicineId(Long medicineId);
}
