package api.giybat.uz.controller;

import api.giybat.uz.dto.RegistrationDTO;
import api.giybat.uz.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<String> registration(@Valid @RequestBody RegistrationDTO dto) {
        return ResponseEntity.ok().body(authService.registrationService(dto));
    }

    @GetMapping("/registration/verification/{profileId}")
    public ResponseEntity<String> regVerification(@PathVariable("profileId") Long profileId) {
        return ResponseEntity.ok().body(authService.regVerification(profileId));
    }

//    @PostMapping("/signin")
//    public ResponseEntity<?> signIn(@RequestBody User user) {
//        User dbUser = userRepository.findByUsername(user.getUsername());
//        if (dbUser != null && new BCryptPasswordEncoder().matches(user.getPassword(), dbUser.getPassword())) {
//            return ResponseEntity.ok(jwtUtil.generateToken(user.getUsername()));
//        }
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//    }
}
