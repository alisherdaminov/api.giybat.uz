package api.giybat.uz.services;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.AuthDto;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.auth.RegistrationDTO;
import api.giybat.uz.dto.auth.ResetPasswordConfirmDTO;
import api.giybat.uz.dto.auth.ResetPasswordDTO;
import api.giybat.uz.dto.sms.SmsResendDTO;
import api.giybat.uz.dto.sms.SmsVerificationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRoleEnum;
import api.giybat.uz.exceptions.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.services.email.EmailHistoryService;
import api.giybat.uz.services.email.EmailSendingService;
import api.giybat.uz.services.sms.SmsHistoryService;
import api.giybat.uz.services.sms.SmsSendService;
import api.giybat.uz.util.EmailUtil;
import api.giybat.uz.util.JwtUtil;
import api.giybat.uz.util.PhoneUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Service
public class AuthService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ProfileRoleService profileRoleService;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;
    @Autowired
    private ResourceBundleService resourceBundleService;
    @Autowired
    private SmsSendService smsSendService;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private EmailHistoryService emailHistoryService;

    public AppResponse<String> registrationService(RegistrationDTO dto, AppLanguage appLanguage) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        // is there a user with this username(checking by username)
        if (optional.isPresent()) { // if username already exists
            ProfileEntity profile = optional.get();
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                //1-usul o' chirib qayta royaxtga olish
                profileRoleService.deleteRole(profile.getId()); // role is deleted
                profileRepository.delete(profile); // profile is deleted
                //2-usul send email&sms
            } else {
                throw new AppBadException(resourceBundleService.getMessage("email.phone.exists", appLanguage));
            }
        }
        // user creation - unless username already exists
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setName(dto.getName());
        profileEntity.setUsername(dto.getUsername());
        profileEntity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        profileEntity.setStatus(GeneralStatus.IN_REGISTRATION);     // user is not fully registered
        profileEntity.setVisible(true);
        profileEntity.setCreated_date(LocalDateTime.now());
        // saved
        profileRepository.save(profileEntity);
        // user role is set up
        profileRoleService.createRole(profileEntity.getId(), ProfileRoleEnum.ROLE_USER);
        //  emailSendingService.sendRegistrationEmail(dto.getUsername(), profileEntity.getId());
        // SMS SEND
        if (PhoneUtil.isValidPhoneNumber(dto.getUsername())) {
            smsSendService.sendRegistrationSms(dto.getUsername());
            // send email
        } else if (EmailUtil.isValidEmail(dto.getUsername())) {
            // bu threadda ishlata olamiz va ishlash tezligini oshirish uchun. yani alohida oqimda ishlash uchun
            CompletableFuture.runAsync(() -> emailSendingService.sendRegistrationEmail(dto.getUsername(), profileEntity.getId()));
        }
        return new AppResponse<>(resourceBundleService.getMessage("registration.success", appLanguage));
    }

    public String registrationEmailVerification(String token, AppLanguage appLanguage) {
        try {
            Integer profileId = JwtUtil.decodeVerificationToken(token);
            ProfileEntity profile = profileService.getById(profileId);
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                //ACTIVE - why- as user is fully registered and can log in,
                // BLOCK - why - user is blocked by admin and then user cannot log in again
                profileRepository.changeStatus(profileId, GeneralStatus.ACTIVE);
                return resourceBundleService.getMessage("successfully.verified", appLanguage);
            }
        } catch (JwtException e) {
        }
        throw new AppBadException(resourceBundleService.getMessage("verification.failed", appLanguage));
    }

    public ProfileDTO registrationSmsVerification(SmsVerificationDTO dto, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getPhoneNumber());
        if (optional.isEmpty()) {
            throw new AppBadException(resourceBundleService.getMessage("profile.not.found", language));
        }
        ProfileEntity profile = optional.get();
        if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            profileRepository.changeStatus(profile.getId(), GeneralStatus.ACTIVE);
            throw new AppBadException(resourceBundleService.getMessage("verification.failed", language));
        }
        // Code check
        smsHistoryService.checkPhoneNumberSmsCode(dto.getPhoneNumber(), dto.getCode(), language);
        // ACTIVE - why- as user is fully registered and can log in,
        profileRepository.changeStatus(profile.getId(), GeneralStatus.ACTIVE);
        return getProfileLogInResponse(profile);
    }

    public AppResponse<String> registrationSmsVerificationResend(SmsResendDTO dto, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getPhoneNumber());
        if (optional.isEmpty()) {
            throw new AppBadException(resourceBundleService.getMessage("profile.not.found", language));
        }
        ProfileEntity profile = optional.get();
        if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            profileRepository.changeStatus(profile.getId(), GeneralStatus.ACTIVE);
            throw new AppBadException(resourceBundleService.getMessage("verification.failed", language));
        }
        smsSendService.sendRegistrationSms(dto.getPhoneNumber());
        return new AppResponse<>(resourceBundleService.getMessage("registration.success", language));
    }

    public ProfileDTO login(AuthDto dto, AppLanguage appLanguage) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        // Checking all conditions
        /// //////////////////////////////////////
        if (optional.isEmpty()) {
            //400 - bad request
            throw new AppBadException(resourceBundleService.getMessage("user.not.found", appLanguage));
        }
        /// //////////////////////////////////////
        ProfileEntity profile = optional.get();
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), profile.getPassword())) {
            //400 - bad request
            throw new AppBadException(resourceBundleService.getMessage("user.not.found", appLanguage));
        }
        /// //////////////////////////////////////
//        if (!optional.get().getStatus().equals(GeneralStatus.ACTIVE)) {
//            //400 - bad request
//            throw new AppBadException("Wrong status");
//        }
        // //////////////////////////////////////
        // Response
        return getProfileLogInResponse(profile);
    }

    public AppResponse<String> resetPassword(ResetPasswordDTO dto, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()) {
            throw new AppBadException(resourceBundleService.getMessage("user.not.found", language));
        }
        if (!optional.get().getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new AppBadException(resourceBundleService.getMessage("wrong.status", language));
        }
        if (PhoneUtil.isValidPhoneNumber(dto.getUsername())) {
            smsSendService.sendResetPassword(dto.getUsername(), language);
        } else if (EmailUtil.isValidEmail(dto.getUsername())) {
            emailSendingService.sendResetPasswordEmail(dto.getUsername(), language);
        }
        String message = resourceBundleService.getMessage("reset.password.message", language);
        return new AppResponse<>(String.format(message, dto.getUsername()));
    }

    public AppResponse<String> resetPasswordConfirm(ResetPasswordConfirmDTO dto, AppLanguage language) {
        // user check
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()) {
            throw new AppBadException(resourceBundleService.getMessage("user.not.found", language));
        }
        // user status check
        ProfileEntity profile = optional.get();
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            throw new AppBadException(resourceBundleService.getMessage("wrong.status", language));
        }
        //code check
        if (PhoneUtil.isValidPhoneNumber(dto.getUsername())) {
            smsHistoryService.checkPhoneNumberSmsCode(dto.getUsername(), dto.getConfirmationCode(), language);
        } else if (EmailUtil.isValidEmail(dto.getUsername())) {
            emailHistoryService.checkEmailSmsCode(dto.getUsername(), dto.getConfirmationCode(), language);
        }
        // password update
        profileRepository.updatePassword(profile.getId(), bCryptPasswordEncoder.encode(dto.getPassword()));
        return new AppResponse<>(resourceBundleService.getMessage("reset.password.updated.successfully", language));
    }

    public ProfileDTO getProfileLogInResponse(ProfileEntity profile) {
        // Response
        ProfileDTO response = new ProfileDTO();
        response.setId(profile.getId());
        response.setName(profile.getName());// name set
        response.setUsername(profile.getUsername());// username set
        response.setRolesList(profileRoleRepository.findAllRolesByProfileId(profile.getId())); // roles set
        response.setJwt(JwtUtil.encode(profile.getUsername(), profile.getId(), response.getRolesList()));// token is created
        return response;
    }


}
