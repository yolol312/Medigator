// CompareDTO
package com.medigator.medigator.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CompareDTO {
    private String diseaseName;
    private List<MedicineDTO> items;
    private int matching_count;
    private int all_count;
    private List<Boolean> types;
}