package dekim.aa_backend.controller;


import dekim.aa_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class JoinViewController {
    @Autowired
    AuthService authService;

    @GetMapping("/email_auth")
    public String authenticateUser(@RequestParam("email") String email, @RequestParam("authKey") String authKey, Model model) {
        try {
            authService.checkEmailWithAuthKey(email, authKey);

            model.addAttribute("message", "이메일 인증이 완료되었습니다!");
            return "auth";

        } catch (IllegalArgumentException e) {
            System.out.println("Exception message: " + e.getMessage());

            model.addAttribute("message", "이메일 인증에 실패하였습니다.");
            return "error";
        }
    }
}