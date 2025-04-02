package api.giybat.uz.controller;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.AuthDto;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.auth.RegistrationDTO;
import api.giybat.uz.dto.auth.ResetPasswordConfirmDTO;
import api.giybat.uz.dto.auth.ResetPasswordDTO;
import api.giybat.uz.dto.sms.SmsResendDTO;
import api.giybat.uz.dto.sms.SmsVerificationDTO;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<AppResponse<String>> registration(@Valid @RequestBody RegistrationDTO dto,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(authService.registrationService(dto, language));
    }

    @GetMapping("/registration/email-verification/{token}")
    public ResponseEntity<String> registrationEmailVerification(@PathVariable("token") String token,
                                                                //once got email verification link error->   // @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language
                                                                @RequestParam("language") AppLanguage language
    ) {
        return ResponseEntity.ok().body(authService.registrationEmailVerification(token, language));
    }

    @PostMapping("/registration/sms-verification")
    public ResponseEntity<ProfileDTO> registrationSmsVerification(@Valid @RequestBody SmsVerificationDTO dto,
                                                                  @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(authService.registrationSmsVerification(dto, language));
    }

    @PostMapping("/registration/sms-verification-resend")
    public ResponseEntity<AppResponse<String>> registrationSmsVerificationResend(@Valid @RequestBody SmsResendDTO dto,
                                                                                 @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(authService.registrationSmsVerificationResend(dto, language));
    }

    @PostMapping("/login")
    public ResponseEntity<ProfileDTO> login(@Valid @RequestBody AuthDto dto,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(authService.login(dto, language));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<AppResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordDTO dto,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(authService.resetPassword(dto, language));
    }

    @PostMapping("/reset-password-confirm")
    public ResponseEntity<AppResponse<String>> resetPasswordConfirm(@Valid @RequestBody ResetPasswordConfirmDTO dto,
                                                                    @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(authService.resetPasswordConfirm(dto, language));
    }


}
