// MyPageRepository.java

package com.medigator.medigator.repository;

import com.medigator.medigator.dto.MyPageDTO;
import com.medigator.medigator.entity.MedicineInteractionEntity;
import com.medigator.medigator.entity.MyPageEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

// 어떤 엔티티 ,    PK키 자료형
public interface MyPageRepository extends JpaRepository<MyPageEntity, Long> {
    @Service
    public class MyPageService {
        @Autowired
        private MyPageRepository myPageRepository;

        @Autowired
        private MedicineRepository medicineRepository;

    }
}