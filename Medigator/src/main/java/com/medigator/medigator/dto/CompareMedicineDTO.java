// MedicineDTO
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
public class CompareMedicineDTO {
    private MedicineDTO medicine;
    private List<String> items;
    private int matchingcount;
    private int diseasecount;
}