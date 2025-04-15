package api.giybat.uz.services;


import api.giybat.uz.entity.ProfileRoleEntity;
import api.giybat.uz.enums.ProfileRoleEnum;
import api.giybat.uz.repository.ProfileRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProfileRoleService {

    // This is a ProfileRoleService which is used to create a role for a profile

    @Autowired
    private ProfileRoleRepository profileRoleRepository;

    public void createRole(Integer profileId, ProfileRoleEnum profileRole) {
        ProfileRoleEntity entity = new ProfileRoleEntity();
        entity.setProfileId(profileId);
        entity.setRoles(profileRole);
        entity.setCreatedDate(LocalDateTime.now());
        profileRoleRepository.save(entity);
    }

    public void deleteRole(Integer profileId) {
        profileRoleRepository.deleteByProfileId(profileId);
    }
}
