package api.giybat.uz.services;

import api.giybat.uz.dto.AuthDto;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.RegistrationDTO;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRoleEnum;
import api.giybat.uz.exceptions.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import api.giybat.uz.repository.ProfileRoleRepository;
import api.giybat.uz.util.JwtUtil;
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
    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    public String registrationService(RegistrationDTO dto) {
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
                throw new AppBadException("Username already exists");
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


    public ProfileDTO login(AuthDto dto) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        // Checking all conditions
        /// //////////////////////////////////////
        if (optional.isEmpty()) {
            //400 - bad request
            throw new AppBadException("Username or password is incorrect");
        }
        /// //////////////////////////////////////
        ProfileEntity profile = optional.get();
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), profile.getPassword())) {
            //400 - bad request
            throw new AppBadException("Username or password is incorrect");
        }
        /// //////////////////////////////////////
//        if (!optional.get().getStatus().equals(GeneralStatus.ACTIVE)) {
//            //400 - bad request
//            throw new AppBadException("Wrong status");
//        }
        // //////////////////////////////////////
        // Response
        ProfileDTO response = new ProfileDTO();
        response.setId(profile.getId());
        response.setName(profile.getName());// name set
        response.setUsername(profile.getUsername());// username set
        response.setRolesList(profileRoleRepository.findAllRolesByProfileId(profile.getId())); // roles set
        response.setJwt(JwtUtil.encode(profile.getId(), response.getRolesList()));// token is created
        return response;
    }
}
