// MedicineDTO
package com.medigator.medigator.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MedicineDTO {
    private Long id;
    private String entpName;
    private String itemName;
    private String efcyQesitm;
    private String atpnQesitm;
    private String useMethodQesitm;
    private String intrcQesitm;
}
