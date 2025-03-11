package api.giybat.uz.controller;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.AuthDto;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.RegistrationDTO;
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
                                                            @RequestHeader("Accept-Language") AppLanguage language) {
        return ResponseEntity.ok().body(authService.registrationService(dto, language));
    }

    @PostMapping("/login")
    public ResponseEntity<ProfileDTO> login(@Valid @RequestBody AuthDto dto,
                                            @RequestHeader("Accept-Language") AppLanguage language) {
        return ResponseEntity.ok().body(authService.login(dto, language));
    }

    @GetMapping("/registration/verification/{profileId}")
    public ResponseEntity<String> regVerification(@PathVariable("profileId") Long profileId,
                                                  @RequestHeader("Accept-Language") AppLanguage language) {
        return ResponseEntity.ok().body(authService.regVerification(profileId, language));
    }


}
