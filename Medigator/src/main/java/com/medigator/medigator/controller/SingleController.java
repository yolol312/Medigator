// MyPageController.java

package com.medigator.medigator.controller;

import com.medigator.medigator.dto.*;
import com.medigator.medigator.service.MyPageService;
import com.medigator.medigator.service.SingleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class SingleController {
    private final SingleService singleService;

    @GetMapping("/SingleNegative/diseases")
    @ResponseBody
    public ResponseEntity<CompareMedicineDTO> getMyDiseases(@ModelAttribute MedicineDTO medicine, HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<String> diseases = singleService.getDiseasesByMemberId(memberId);
        CompareMedicineDTO compares = singleService.getDiseasesCompare(diseases, medicine);
        if (compares == null) {
            return ResponseEntity.ok().body(new CompareMedicineDTO());
        }
        return ResponseEntity.ok(compares);
    }

    @GetMapping("/SinglePositive/diseases")
    @ResponseBody
    public ResponseEntity<CompareMedicineDTO> getMyDiseasesPositive(@ModelAttribute MedicineDTO medicine, HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<String> diseases = singleService.getDiseasesByMemberId(memberId);
        CompareMedicineDTO compares = singleService.getDiseasesComparePositive(diseases, medicine);
        if (compares == null) {
            return ResponseEntity.ok().body(new CompareMedicineDTO());
        }
        return ResponseEntity.ok(compares);
    }
}