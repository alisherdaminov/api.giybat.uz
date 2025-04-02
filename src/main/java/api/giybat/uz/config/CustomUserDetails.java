package api.giybat.uz.config;

import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    // Spring securtyga qaysi user datalarni bermoqchimiz va
    // hozr murojat qilnayotgan user kk bolsa uni qaysi fieldlari kk boladi .
    // keynchalik nimadir narsa kk bolsa biz shu joyga qoshib boramiz
    //CustomUserDetailsService classiga CustomUserDetailsni qoshamiz

    private Integer id;
    private String name;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private GeneralStatus status;

    public CustomUserDetails(ProfileEntity profile,
                             List<ProfileRoleEnum> profileRoleEnumList) {
        this.id = profile.getId();
        this.name = profile.getName();
        this.username = profile.getUsername();
        this.password = profile.getPassword();
        this.status = profile.getStatus();

        //1-ususl
        this.authorities = profileRoleEnumList.stream().map(role -> new SimpleGrantedAuthority(role.name())).toList();

        //2- ususl
//        List<SimpleGrantedAuthority> roles = new ArrayList<>();
//        for (ProfileRoleEnum role : profileRoleEnumList) {
//            roles.add(new SimpleGrantedAuthority(role.name()));
//        }
//        this.authorities = roles;

    }


    // Bu - GrantedAuthority - profiledagi rolesni return qiladi
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // mudati o'tgan user bo'lsa false qaytaradi , agar o'tmagan bo'lsa true qaytaradi
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.equals(GeneralStatus.ACTIVE);
    }

    // password har 2 oyda (Misol un ishlatilmagan bolsa user tomonidan) o'tgan bo'lsa false qaytaradi,
    // agar o'tmagan bo'lsa true qaytaradi
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
