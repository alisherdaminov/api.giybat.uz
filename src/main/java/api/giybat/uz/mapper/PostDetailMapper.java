package api.giybat.uz.mapper;

import api.giybat.uz.enums.GeneralStatus;

import java.time.LocalDateTime;

public interface PostDetailMapper {
    String getPostId();

    String getPostTitle();

    String getPostPhotoId();

    GeneralStatus getStatus();

    LocalDateTime getPostCreatedDate();

    Integer getProfileId();

    String getProfileName();

    String getProfileUsername();

    Long getPostCount();

    String getRoles();
}
