package com.medigator.medigator.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MedicineInteractionDTO {
    private long interactionId;
    private long memberId;
    private long medicineId;
    private String drugInteractionDanger;
    private String drugInteractionWarn;
    private String drugFoodInteraction;
    private String drugConditionInteraction;
}
