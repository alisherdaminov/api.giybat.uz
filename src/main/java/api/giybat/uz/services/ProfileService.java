package api.giybat.uz.services;

import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.exceptions.AppBadException;
import api.giybat.uz.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {


    // ProfileService is used to get profile by id for email sending
    @Autowired
    private ProfileRepository profileRepository;

    public ProfileEntity getById(Long id) {
        return profileRepository.findByIdAndVisibleTrue(id).orElseThrow(() -> new AppBadException("Profile not found"));
    }
}
