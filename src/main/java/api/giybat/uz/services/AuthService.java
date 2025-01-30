package api.giybat.uz.services;

import api.giybat.uz.dto.RegistrationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRoleEnum;
import api.giybat.uz.exceptions.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


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

    public String registrationService(RegistrationDTO dto) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent()) {
            ProfileEntity profile = optional.get();
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                //1-usul o' chirib qayta royaxtga olish
                profileRoleService.deleteRole(profile.getId()); // role is deleted
                profileRepository.delete(profile); // profile is deleted
                //2-usul send email&sms
            } else {
                throw new AppBadException("Username already exists");
            }
        }
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
        // send email
        emailSendingService.sendRegistrationEmail(dto.getUsername(), profileEntity.getId());
        return "Successfully registered!";
    }


    public String regVerification(Long profileId) {
        ProfileEntity profile = profileService.getById(profileId);
        if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            //ACTIVE - why- as user is fully registered and can log in,
            // BLOCK - why - user is blocked by admin and then user cannot log in again
            profileRepository.changeStatus(profileId, GeneralStatus.ACTIVE);
            return "Successfully verified!";
        }
        throw new AppBadException("Verification failed");
    }
}
