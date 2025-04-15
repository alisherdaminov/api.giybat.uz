package api.giybat.uz.services;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.CodeConfirmDTO;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.profile.ProfileDetailUpdateDTO;
import api.giybat.uz.dto.profile.ProfileFilterDTO;
import api.giybat.uz.dto.profile.ProfilePasswordUpdateDTO;
import api.giybat.uz.dto.profile.ProfileUsernameUpdateDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.entity.ProfileRoleEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRoleEnum;
import api.giybat.uz.exceptions.AppBadException;
import api.giybat.uz.mapper.ProfileDetailMapper;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.services.email.EmailHistoryService;
import api.giybat.uz.services.email.EmailSendingService;
import api.giybat.uz.services.image.AttachService;
import api.giybat.uz.services.sms.SmsHistoryService;
import api.giybat.uz.services.sms.SmsSendService;
import api.giybat.uz.util.EmailUtil;
import api.giybat.uz.util.JwtUtil;
import api.giybat.uz.util.PhoneUtil;
import api.giybat.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
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
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private EmailHistoryService emailHistoryService;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;
    @Autowired
    private AttachService attachService;

    // profilega tegishli id ni olish
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

    public AppResponse<String> updateUsernameConfirm(CodeConfirmDTO codeConfirmDTO, AppLanguage language) {
        Integer userId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profileId = getById(userId);
        String tempUsername = profileId.getTempUsername();
        //code check
        if (PhoneUtil.isValidPhoneNumber(tempUsername)) {
            smsHistoryService.checkPhoneNumberSmsCode(tempUsername, codeConfirmDTO.getCode(), language);
        } else if (EmailUtil.isValidEmail(tempUsername)) {
            emailHistoryService.checkEmailSmsCode(tempUsername, codeConfirmDTO.getCode(), language);
        }
        profileRepository.updateUsername(userId, tempUsername);

        List<ProfileRoleEnum> roleEnums = profileRoleRepository.findAllRolesByProfileId(userId);
        // Token has to be resent to front end for reuse successfully user's data
        String jwt = JwtUtil.encode(tempUsername, profileId.getId(), roleEnums);
        return new AppResponse<>(jwt, resourceBundleService.getMessage("username.successfully.changed", language));
    }

    public AppResponse<String> updatePhoto(String photoId, AppLanguage language) {
        Integer userId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profile = getById(userId);
        profileRepository.updatePhoto(userId, photoId);// update new photo

        // old photo what to do ...?
        if (profile.getPhotoId() != null && !profile.getPhotoId().equals(photoId)) {
            attachService.updatePhoto(profile.getPhotoId());// update old photo
        }
        return new AppResponse<>(resourceBundleService.getMessage("profile.photo.updated.successfully", language));
    }

    public AppResponse<String> profileStatusChangeDTO(Integer id, GeneralStatus status, AppLanguage language) {
        profileRepository.changeStatus(id, status);
        return new AppResponse<>(resourceBundleService.getMessage("profile.status.updated.successfully", language));
    }

    public AppResponse<String> deleteAsVisibleFalse(Integer id, AppLanguage language) {
        profileRepository.deleteAsVisibleFalse(id);
        return new AppResponse<>(resourceBundleService.getMessage("profile.deleted.successfully", language));
    }

    public PageImpl<ProfileDTO> profileFilterPosts(ProfileFilterDTO dto, AppLanguage language, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProfileDetailMapper> filterResult = null;
        if (dto.getQuery() == null) {
            // once data null == all users
            filterResult = profileRepository.filterByVisibleAsInnerJoinWithSubQuery(pageRequest);

        } else {
            // once searched all users
            filterResult = profileRepository.filterByUsernameAndNameAsInnerJoin(dto.getQuery().toLowerCase(), pageRequest);
        }
        List<ProfileDTO> resultList = filterResult.stream().map(this::toMapperDTO).toList();
        return new PageImpl<>(resultList, pageRequest, filterResult.getTotalElements());
    }

    public ProfileDTO toDTO(ProfileEntity entity) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(entity.getId());
        profileDTO.setName(entity.getName());
        profileDTO.setUsername(entity.getUsername());
        if (entity.getRoleList() != null) {
            List<ProfileRoleEnum> roleList = entity.getRoleList().stream().map(ProfileRoleEntity::getRoles).toList();
            profileDTO.setRolesList(roleList);
        }
        profileDTO.setAttachDTO(attachService.attachDTO(entity.getPhotoId()));
        profileDTO.setPostCount(entity.getPostsCount());
        profileDTO.setStatus(entity.getStatus());
        profileDTO.setCreatedDate(entity.getCreatedDate());
        return profileDTO;
    }

    public ProfileDTO toMapperDTO(ProfileDetailMapper mapper) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(mapper.getId());
        profileDTO.setName(mapper.getName());
        profileDTO.setUsername(mapper.getUsername());
        if (mapper.getRoles() != null) {
            List<ProfileRoleEnum> roleList = Arrays.stream(mapper.getRoles().split(","))
                    .map(ProfileRoleEnum::valueOf).toList();
            profileDTO.setRolesList(roleList);
        }
        profileDTO.setAttachDTO(attachService.attachDTO(mapper.getPhotoId()));
        profileDTO.setPostCount(mapper.getPostCount());
        profileDTO.setStatus(mapper.getStatus());
        profileDTO.setCreatedDate(mapper.getCreatedDate());
        return profileDTO;
    }

}
