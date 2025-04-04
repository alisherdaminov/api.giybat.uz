package api.giybat.uz.services;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.profile.ProfileDetailUpdateDTO;
import api.giybat.uz.dto.profile.ProfilePasswordUpdateDTO;
import api.giybat.uz.dto.profile.ProfileUsernameUpdateDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.exceptions.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.services.email.EmailSendingService;
import api.giybat.uz.services.sms.SmsSendService;
import api.giybat.uz.util.EmailUtil;
import api.giybat.uz.util.PhoneUtil;
import api.giybat.uz.util.SpringSecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ResourceBundleService resourceBundleService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SmsSendService smsSendService;
    @Autowired
    private EmailSendingService emailSendingService;

    public ProfileEntity getById(Integer id) {
        return profileRepository.findByIdAndVisibleTrue(id).orElseThrow(() -> new AppBadException("Profile not found"));
    }


    public AppResponse<String> updateDetail(ProfileDetailUpdateDTO profileDetailUpdateDTO, AppLanguage language) {
        Integer userId = SpringSecurityUtil.getCurrentUserId();
        profileRepository.updateDetail(userId, profileDetailUpdateDTO.getName());
        return new AppResponse<>(resourceBundleService.getMessage("profile.updated.successfully", language));
    }

    public AppResponse<String> updatePassword(ProfilePasswordUpdateDTO passwordUpdateDTO, AppLanguage language) {
        Integer userId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profileId = getById(userId);
        if (!bCryptPasswordEncoder.matches(passwordUpdateDTO.getCurrentPassword(), profileId.getPassword())) {
            throw new AppBadException(resourceBundleService.getMessage("wrong.password", language));
        }
        profileRepository.updatePassword(userId, bCryptPasswordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        return new AppResponse<>(resourceBundleService.getMessage("profile.password.updated.successfully", language));
    }

    public AppResponse<String> updateUsername(ProfileUsernameUpdateDTO profileUsernameUpdateDTO, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(profileUsernameUpdateDTO.getUsername());
        if (optional.isPresent()) {
            throw new AppBadException(resourceBundleService.getMessage("email.phone.exists", language));
        }
        //SEND
        if (PhoneUtil.isValidPhoneNumber(profileUsernameUpdateDTO.getUsername())) {
            smsSendService.sendPhoneUsernameChangeConfirmationSms(profileUsernameUpdateDTO.getUsername(), language);
        } else if (EmailUtil.isValidEmail(profileUsernameUpdateDTO.getUsername())) {
            emailSendingService.sendUsernameChangeEmail(profileUsernameUpdateDTO.getUsername(), language);
        }
        // SAVE
        Integer userId = SpringSecurityUtil.getCurrentUserId();
        profileRepository.updateTempUsername(userId, profileUsernameUpdateDTO.getUsername());
        //responseMessage for sms and email code shows once sms and email is received
        String responseMessage = resourceBundleService.getMessage("username.update.message", language);
        return new AppResponse<>(String.format(responseMessage, profileUsernameUpdateDTO.getUsername()));
    }
}
