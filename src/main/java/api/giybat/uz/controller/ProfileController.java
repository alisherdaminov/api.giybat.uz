package api.giybat.uz.controller;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.CodeConfirmDTO;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.profile.*;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.services.ProfileService;
import api.giybat.uz.util.PageUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "ProfileController", description = "Profile API's for user details update")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PutMapping("/update-detail")
    public ResponseEntity<AppResponse<String>> updateDetail(@Valid @RequestBody ProfileDetailUpdateDTO profileDetailUpdateDTO,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(profileService.updateDetail(profileDetailUpdateDTO, language));
    }

    @PutMapping("/update-photo")
    public ResponseEntity<AppResponse<String>> updatePhoto(@Valid @RequestBody ProfilePhotoUpdateDTO profilePhotoUpdateDTO,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(profileService.updatePhoto(profilePhotoUpdateDTO.getPhotoId(), language));
    }


    @PutMapping("/update-password")
    public ResponseEntity<AppResponse<String>> updatePassword(@Valid @RequestBody ProfilePasswordUpdateDTO passwordUpdateDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(profileService.updatePassword(passwordUpdateDTO, language));
    }


    @PutMapping("/update-username")
    public ResponseEntity<AppResponse<String>> updateUsername(@Valid @RequestBody ProfileUsernameUpdateDTO profileUsernameUpdateDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(profileService.updateUsername(profileUsernameUpdateDTO, language));
    }

    @PutMapping("/update-username/confirm")
    public ResponseEntity<AppResponse<String>> updateUsernameConfirm(@Valid @RequestBody CodeConfirmDTO codeConfirmDTO,
                                                                     @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(profileService.updateUsernameConfirm(codeConfirmDTO, language));
    }

    @PutMapping("/change-status/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppResponse<String>> profileStatusChangeDTO(@PathVariable("id") Integer id,
                                                                      @RequestBody ProfileStatusChangeDTO dto,
                                                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(profileService.profileStatusChangeDTO(id, dto.getStatus(), language));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppResponse<String>> deleteAsVisibleFalse(@PathVariable("id") Integer id,
                                                                    @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok().body(profileService.deleteAsVisibleFalse(id, language));
    }


    @PostMapping("/filter-posts")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageImpl<ProfileDTO>> profileFilterPosts(@RequestBody ProfileFilterDTO dto,
                                                                   @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language,
                                                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                                                   @RequestParam(value = "size", defaultValue = "12") int size) {
        return ResponseEntity.ok().body(profileService.profileFilterPosts(dto, language, PageUtil.page(page), size));
    }
}

