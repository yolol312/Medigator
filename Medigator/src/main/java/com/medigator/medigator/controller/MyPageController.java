// MyPageController.java

package com.medigator.medigator.controller;

import com.medigator.medigator.dto.CompareDTO;
import com.medigator.medigator.dto.MedicineDTO;
import com.medigator.medigator.dto.MyDiseaseDTO;
import com.medigator.medigator.dto.MyPageDTO;
import com.medigator.medigator.service.MedicineService;
import com.medigator.medigator.service.MyPageService;
import com.medigator.medigator.service.OpenAiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/member/MyPage")
    public String myPageForm() {
        return "MyPage";
    }

    @PostMapping("/member/updateMyPage")
    @ResponseBody  // JSON 혹은 기타 데이터를 응답 본문에 직접 포함시키기 위한 어노테이션
    public ResponseEntity<Map<String, String>> updateMyPage(@RequestBody MyPageDTO myPageDTO, HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId != null) {
            boolean updateResult = myPageService.updateMyPageInfo(myPageDTO, memberId);
            Map<String, String> response = new HashMap<>();
            if (updateResult) {
                response.put("status", "success");
                response.put("message", "저장되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "저장에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "로그인 필요");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/member/addMedicine")
    @ResponseBody
    public ResponseEntity<?> addMedicine(@RequestBody MedicineDTO medicineDTO, HttpSession session) {
        System.out.println((String)session.getAttribute("loginId"));
        String memberId = (String) session.getAttribute("loginId");
        if (memberId != null && isValidMedicineDTO(medicineDTO)) {
            // 해당 회원이 추가한 모든 약물 조회
            List<MedicineDTO> medicines = myPageService.getMedicinesByMemberId(memberId);

            // 중복 여부 확인
            boolean isDuplicate = medicines.stream().anyMatch(medicine ->
                    medicine.getItemName().equalsIgnoreCase(medicineDTO.getItemName()) &&
                            medicine.getEntpName().equalsIgnoreCase(medicineDTO.getEntpName())
            );

            if (isDuplicate) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 추가된 약물입니다.");
            }

            // 중복되지 않는다면 약물 추가
            myPageService.addMedicine(medicineDTO, memberId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 데이터가 제공되었습니다.");
    }

    private boolean isValidMedicineDTO(MedicineDTO dto) {
        return dto != null && dto.getItemName() != null && !dto.getItemName().isEmpty() &&
                dto.getEntpName() != null && !dto.getEntpName().isEmpty();
    }

    @PostMapping("/member/saveMaterialName")
    @ResponseBody
    public ResponseEntity<String> saveMaterialName(@RequestBody Map<String, String> request, HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId != null) {
            String materialName = request.get("materialName");
            String itemName = request.get("itemName");
            boolean saveResult = myPageService.saveMaterialName(materialName, itemName);
            if (saveResult) {
                return ResponseEntity.ok("MATERIAL_NAME 저장되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("MATERIAL_NAME 저장에 실패했습니다.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
    }

    @GetMapping("/member/mypage/details")
    public ResponseEntity<MyPageDTO> getMyPageDetails(HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        MyPageDTO details = myPageService.getDetailsByMemberId(memberId);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/member/mypage/medicines")
    public ResponseEntity<List<MedicineDTO>> getMyMedicines(HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<MedicineDTO> medicines = myPageService.getMedicinesByMemberId(memberId);
        if (medicines.isEmpty()) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }
        return ResponseEntity.ok(medicines);
    }

    @RestController
    public class MedicineController {
        @Autowired
        private MedicineService medicineService;

        @DeleteMapping("/member/mypage/medicine/{itemName}")
        public ResponseEntity<?> deleteMedicine(HttpSession session, @PathVariable String itemName) {
            boolean success = medicineService.deleteMedicineByItemNameAndSessionMemberId(session, itemName);
            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }

    @RestController
    @RequestMapping("/api")
    public class ApiController {
        private final OpenAiService openAiService;
        private final MedicineService medicineService;

        @Autowired
        public ApiController(OpenAiService openAiService, MedicineService medicineService) {
            this.openAiService = openAiService;
            this.medicineService = medicineService;
        }

        @PostMapping("/summarize")
        public ResponseEntity<?> summarizeInteraction(@RequestBody Map<String, String> requestBody, HttpSession session) {
            String memberId = (String) session.getAttribute("loginId");
            String itemName = requestBody.get("itemName");
            System.out.println("itemName : " + itemName);
            String originalText = requestBody.get("text");
            System.out.println("originalText : " + originalText);
            try {
                Map<String, Object> summary = openAiService.summarizeInteraction(originalText);
                medicineService.saveInteraction(summary, memberId, itemName);
                return ResponseEntity.ok(summary);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Error summarizing text: " + e.getMessage());
            }
        }
    }

    @GetMapping("/member/mypage/diseases")
    public ResponseEntity<List<CompareDTO>> getMyDiseases(HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<String> diseases = myPageService.getDiseasesByMemberId(memberId);
        List<MedicineDTO> medicines = myPageService.getMedicinesByMemberId(memberId);
        List<CompareDTO> compares = myPageService.getDiseasesCompare(diseases, medicines);
        if (compares == null) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }
        return ResponseEntity.ok(compares);
    }

    @GetMapping("/member/mypage/Mydiseases")
    public ResponseEntity<List<String>> getDiseases(HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<String> diseases = myPageService.getDiseasesByMemberId(memberId);
        return ResponseEntity.ok(diseases);
    }

    @PostMapping("/member/updateMyDisease")
    @ResponseBody  // JSON 혹은 기타 데이터를 응답 본문에 직접 포함시키기 위한 어노테이션
    public ResponseEntity<Map<String, String>> updateMyDisease(@RequestBody MyDiseaseDTO myDiseaseDTO, HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId != null) {
            System.out.println(myDiseaseDTO.getDisease1());
            System.out.println(myDiseaseDTO.getDisease2());
            System.out.println(myDiseaseDTO.getDisease3());
            boolean updateResult1 = true;
            boolean updateResult2 = true;
            boolean updateResult3 = true;
            if(!Objects.equals(myDiseaseDTO.getDisease1(), "")){
                updateResult1 = myPageService.updateMyDiseaseInfo(myDiseaseDTO.getDisease1(), memberId);
            }
            if(!Objects.equals(myDiseaseDTO.getDisease2(), "")){
                updateResult2 = myPageService.updateMyDiseaseInfo(myDiseaseDTO.getDisease2(), memberId);
            }
            if(!Objects.equals(myDiseaseDTO.getDisease3(), "")){
                updateResult3 = myPageService.updateMyDiseaseInfo(myDiseaseDTO.getDisease3(), memberId);
            }
            Map<String, String> response = new HashMap<>();
            if (updateResult1 && updateResult2 && updateResult3) {
                response.put("status", "success");
                response.put("message", "저장되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "저장에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "로그인 필요");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/member/deleteMyDisease")
    @ResponseBody  // JSON 혹은 기타 데이터를 응답 본문에 직접 포함시키기 위한 어노테이션
    public ResponseEntity<Map<String, String>> deleteMyDisease(@RequestBody String myDisease, HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId != null) {
            boolean deleteResult1 = true;
            if(!"".equals(myDisease)){
                System.out.println(myDisease);
                deleteResult1 = myPageService.deleteMyDiseaseInfo(myDisease, memberId);
            }
            Map<String, String> response = new HashMap<>();
            if (deleteResult1) {
                response.put("status", "success");
                response.put("message", "삭제되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "삭제 실패했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "로그인 필요");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

}
