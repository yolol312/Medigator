package com.medigator.medigator.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MaterialNameDTO {
    private long id;
    private long medicineId;
    private String itemName;
    private String materialName;
}
