package com.medigator.medigator.service;

import com.medigator.medigator.dto.MaterialNameDTO;
import com.medigator.medigator.entity.MaterialNameEntity;
import com.medigator.medigator.entity.MedicineEntity;
import com.medigator.medigator.repository.MaterialNameRepository;
import com.medigator.medigator.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class MaterialNameService {

    @Autowired
    private MaterialNameRepository materialNameRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    @Transactional
    public MaterialNameDTO fetchAndSaveMaterialName(String itemName, Long medicineId) {
        // 외부 API 호출
        String apiUrl = "http://apis.data.go.kr/1471000/DrugPrdtPrmsnInfoService05/getDrugPrdtPrmsnInfoList";
        String serviceKey = "pTNRi22KX1NRbOD7ZWsTngtCRegEP2WII0o+NvLPeE3/tpJ+aUtv7Dhf2pR1iv4AjLolK/IyGeJObgLGREO5Dg==\n";

        URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("serviceKey", serviceKey)
                .queryParam("item_name", itemName)
                .queryParam("type", "json")
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        MaterialNameDTO materialNameDTO = restTemplate.getForObject(uri, MaterialNameDTO.class);

        // DB에 저장
        MedicineEntity medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid medicine ID"));

        MaterialNameEntity materialNameEntity = new MaterialNameEntity();
        materialNameEntity.setMedicine(medicine);
        materialNameEntity.setItemName(itemName);
        materialNameEntity.setMaterialName(materialNameDTO.getMaterialName());

        materialNameRepository.save(materialNameEntity);

        return new MaterialNameDTO(
                materialNameEntity.getId(),
                materialNameEntity.getMedicine().getId(),
                materialNameEntity.getItemName(),
                materialNameEntity.getMaterialName()
        );
    }
}
