// MyPageService.java

package com.medigator.medigator.service;

import com.medigator.medigator.dto.*;
import com.medigator.medigator.entity.MedicineEntity;
import com.medigator.medigator.entity.MemberEntity;
import com.medigator.medigator.entity.MyDiseaseEntity;
import com.medigator.medigator.entity.MyPageEntity;
import com.medigator.medigator.repository.MedicineRepository;
import com.medigator.medigator.repository.MemberRepository;
import com.medigator.medigator.repository.MyDiseaseRepository;
import com.medigator.medigator.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

// spring bean으로 등록
@Service
@RequiredArgsConstructor
@Transactional

public class SingleService {
    @Autowired
    private final MemberRepository memberRepository;

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

    public CompareMedicineDTO getDiseasesCompare(List<String> diseases, MedicineDTO medicine) {
        CompareMedicineDTO matchingDiseases = new CompareMedicineDTO();
        int diseasecount = diseases.size();
        int matchingcount = 0;
        List<String> contect_diseases = new ArrayList<>();
        for (String disease : diseases) {
            if (medicine.getAtpnQesitm() != null && medicine.getAtpnQesitm().contains(disease) && !medicine.getEfcyQesitm().contains(disease)) {
                contect_diseases.add(disease);
                matchingcount++;
            }
        }
        matchingDiseases = new CompareMedicineDTO(medicine, contect_diseases, matchingcount, diseasecount);
        return matchingDiseases;
    }

    public CompareMedicineDTO getDiseasesComparePositive(List<String> diseases, MedicineDTO medicine) {
        CompareMedicineDTO matchingDiseases = new CompareMedicineDTO();
        List<String> contect_diseases = new ArrayList<>();
        int diseasecount = diseases.size();
        int matchingcount = 0;
        for (String disease : diseases) {
            if (medicine.getEfcyQesitm() != null && medicine.getEfcyQesitm().contains(disease) && !medicine.getAtpnQesitm().contains(disease)) {
                contect_diseases.add(disease);
                matchingcount++;
            }
        }
        matchingDiseases = new CompareMedicineDTO(medicine, contect_diseases, matchingcount, diseasecount);

        return matchingDiseases;
    }
}

