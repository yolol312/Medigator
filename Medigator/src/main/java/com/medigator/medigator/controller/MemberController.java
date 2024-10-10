package com.medigator.medigator.controller;

import com.medigator.medigator.dto.MemberDTO;
import com.medigator.medigator.service.LoginResponse;
import com.medigator.medigator.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/member/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/member/join")
    public String joinForm() {
        return "join";
    }

    @PostMapping("/member/join")
    public String join(@ModelAttribute MemberDTO memberDTO) {
        memberService.join(memberDTO);
        return "redirect:/";
    }

    @GetMapping("/member/Single")
    public String SingleForm() {
        return "Single";
    }

    @GetMapping("/member/Analysis")
    public String Analysis() {
        return "Analysis";
    }

    @PostMapping("/member/login")
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session, RedirectAttributes redirectAttributes) {
        LoginResponse loginResult = memberService.login(memberDTO);
        if (loginResult.isSuccess()) {
            session.setAttribute("loginId", loginResult.getMemberDTO().getMemberId());
            return "redirect:/index";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", loginResult.getMessage());
            return "redirect:/member/login";
        }
    }

    @GetMapping("/member/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/member/id-check")
    @ResponseBody
    public String idCheck(@RequestParam("memberId") String memberId) {
        return memberService.idCheck(memberId);
    }

    @PostMapping("/member/email-check")
    @ResponseBody
    public String emailCheck(@RequestParam("memberEmail") String memberEmail) {
        return memberService.emailCheck(memberEmail);
    }
}
