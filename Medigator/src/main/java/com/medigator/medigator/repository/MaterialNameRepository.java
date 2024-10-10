package com.medigator.medigator.repository;

import com.medigator.medigator.entity.MaterialNameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MaterialNameRepository extends JpaRepository<MaterialNameEntity, Long> {
    Optional<MaterialNameEntity> findByMedicineId(Long medicineId);


    void deleteByMedicineId(Long medicineId);
}
