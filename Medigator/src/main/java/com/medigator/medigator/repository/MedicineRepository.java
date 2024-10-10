package com.medigator.medigator.repository;

import com.medigator.medigator.entity.MedicineEntity;
import com.medigator.medigator.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<MedicineEntity, Long> {
    List<MedicineEntity> findAllByItemName(String itemName);

    Optional<MedicineEntity> findFirstByItemName(String itemName);

    List<MedicineEntity> findByMemberId(Long memberId);

    List<MedicineEntity> findByMember(MemberEntity member);

    MedicineEntity findByItemNameAndMemberId(String itemName, Long memberId);

    Optional<MedicineEntity> findById(Long Id);
}

