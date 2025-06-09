package api.giybat.uz.util;

import api.giybat.uz.config.CustomUserDetails;
import api.giybat.uz.enums.ProfileRoleEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class SpringSecurityUtil {

    //murojat qilayotgan userni spring securitydan olish SpringSecurityUtil classi yordamida

    public static CustomUserDetails getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }

    public static Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    // for admin or user roles checking true or false response.
    public static Boolean hasRole(ProfileRoleEnum requiredRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().anyMatch(authority ->
                authority.getAuthority().equals(requiredRole.name()));
    }
}
