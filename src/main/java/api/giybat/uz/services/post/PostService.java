package api.giybat.uz.services.post;

import api.giybat.uz.dto.AppResponse;
import api.giybat.uz.dto.ProfileDTO;
import api.giybat.uz.dto.post.*;
import api.giybat.uz.entity.PostEntity;
import api.giybat.uz.entity.ProfileEntity;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.enums.GeneralStatus;
import api.giybat.uz.enums.ProfileRoleEnum;
import api.giybat.uz.exceptions.AppBadException;
import api.giybat.uz.mapper.PostDetailMapper;
import api.giybat.uz.repository.CustomPostRepository;
import api.giybat.uz.repository.PostRepository;
import api.giybat.uz.services.ResourceBundleService;
import api.giybat.uz.services.image.AttachService;
import api.giybat.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private AttachService attachService;
    @Autowired
    private ResourceBundleService resourceBundleService;
    @Autowired
    private CustomPostRepository customRepository;

    public PostDTO createPost(PostCreatedDTO postCreatedDTO) {
        PostEntity entity = new PostEntity();
        entity.setTitle(postCreatedDTO.getTitle());
        entity.setContent(postCreatedDTO.getContent());
        entity.setPhotoId(postCreatedDTO.getPhoto().getId());// PostCreatedDTO da photo id si bor
        entity.setProfileId(SpringSecurityUtil.getCurrentUserId());// post qilayotgan user id si orqali, kim post qilganini bilish m-n
        entity.setStatus(GeneralStatus.IN_CHECKING);
        postRepository.save(entity);
        return toInfoDTO(entity);
    }

    // profilega tegishli postlarni paging orqali olib keladi
    public PageImpl<PostDTO> getProfilePostList(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Integer userId = SpringSecurityUtil.getCurrentUserId();
        Page<PostEntity> postEntities = postRepository.findAllByProfileIdAndVisibleTrueOrderByCreatedDateDesc(userId, pageRequest);
        List<PostDTO> postDTOList = postEntities.getContent().stream().map(this::toInfoDTO).toList();
        return new PageImpl<>(postDTOList, pageRequest, postEntities.getTotalElements());
    }

    // profilega tegishli postlardan biri boslganda keyngi pagda gi datalar olinadi (GET) id orqali
    public PostDTO getProfilePostById(String id) {
        PostEntity postEntity = get(id);
        return toDTO(postEntity);
    }

    public PostDTO update(String id, PostCreatedDTO postCreatedDTO, AppLanguage language) {
        PostEntity postEntity = get(id);
        Integer userId = SpringSecurityUtil.getCurrentUserId();
        //ROLE_ADMIN bolmasa You do not have permission to update this post
        if (!SpringSecurityUtil.hasRole(ProfileRoleEnum.ROLE_ADMIN) && !postEntity.getProfileId().equals(userId)) {
            throw new AppBadException(resourceBundleService.getMessage("you.do.not.have.permission.to.update.this.post", language));
        }
        // rasmni tekshirish
        String oldPhotoId = null;
        if (!postCreatedDTO.getPhoto().getId().equals(postEntity.getPhotoId())) {
            oldPhotoId = postEntity.getPhotoId();
        }
        postEntity.setTitle(postCreatedDTO.getTitle());
        postEntity.setContent(postCreatedDTO.getContent());
        postEntity.setPhotoId(postCreatedDTO.getPhoto().getId());
        postRepository.save(postEntity);
        if (oldPhotoId != null) {
            attachService.updatePhoto(oldPhotoId);
        }
        return toInfoDTO(postEntity);

    }

    public AppResponse<String> deleteByVisibleFalse(String id, AppLanguage language) {
        /**  PostEntity postEntity = get(id); -------> for delete from database
         postRepository.delete(postEntity);
         return true;**/
        PostEntity postEntity = get(id);
        Integer userId = SpringSecurityUtil.getCurrentUserId();
        if (!SpringSecurityUtil.hasRole(ProfileRoleEnum.ROLE_ADMIN) && !postEntity.getProfileId().equals(userId)) {// admin hammas && egasi hammas
            throw new AppBadException(resourceBundleService.getMessage("you.do.not.have.permission.to.delete.this.post", language));
        }
        postRepository.deleteByVisibleFalse(id);
        return new AppResponse<>(resourceBundleService.getMessage("post.deleted.successfully", language));
    }

    public List<PostDTO> getSimilarPostList(SimilarPostListDTO dto) {
        List<PostEntity> postEntities = postRepository.getSimilarPostList(dto.getExceptPostId());
        return postEntities.stream().map(this::toInfoDTO).toList();
    }

    public PageImpl<PostDTO> postPublicFilter(PostPublicFilterDTO filter, int page, int size) {
        FilterResultDTO<PostEntity> filterResultDTO = customRepository.filterResultPublicDTO(filter, page, size);
        List<PostDTO> postDTOList = filterResultDTO.getPostEntities().stream().map(this::toInfoDTO).toList(); // postlar listini olib keladi>
        return new PageImpl<>(postDTOList, PageRequest.of(page, size), filterResultDTO.getCount());
    }

    public PageImpl<PostDTO> postAdminFilter(PostAdminFilterDTO dto, int page, int size) {
        FilterResultDTO<Object[]> filterResultDTO = customRepository.filterResultAdminDTO(dto, page, size);
        List<PostDTO> postDTOList = filterResultDTO.getPostEntities().stream().map(this::toPostDetailMapperDTO).toList(); // postlar listini olib keladi>
        return new PageImpl<>(postDTOList, PageRequest.of(page, size), filterResultDTO.getCount());
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////too much use

    public PostDTO toPostDetailMapperDTO(Object[] objMapper) {

        //  POST
        PostDTO postDTO = new PostDTO();
        postDTO.setId((String) objMapper[0]);
        postDTO.setTitle((String) objMapper[1]);
        if (objMapper[2] != null) {
            postDTO.setPhoto(attachService.attachDTO((String) objMapper[2]));
        }
        postDTO.setCreatedDate((LocalDateTime) objMapper[3]);
        //profile
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId((Integer) objMapper[4]);
        profileDTO.setName((String) objMapper[5]);
        profileDTO.setUsername((String) objMapper[6]);

        postDTO.setProfile(profileDTO);
        return postDTO;
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////too much use
    // toDTO is for all data includes content
    public PostDTO toDTO(PostEntity entity) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(entity.getId());
        postDTO.setTitle(entity.getTitle());
        postDTO.setContent(entity.getContent());
        postDTO.setCreatedDate(entity.getCreatedDate());// visible true larni send qilamiz, visible false lar methodga kelmaydi
        postDTO.setPhoto(attachService.attachDTO(entity.getPhotoId()));//attachService.attachDTO() orqali rasmni URL VA ID sini jonatadi
        return postDTO;
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////too much use
    // toInfoDTO is for without content profilega tegishli postlar list uchun
    public PostDTO toInfoDTO(PostEntity entity) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(entity.getId());
        postDTO.setTitle(entity.getTitle());
        postDTO.setCreatedDate(entity.getCreatedDate());// visible true larni send qilamiz, visible false lar methodga kelmaydi
        postDTO.setPhoto(attachService.attachDTO(entity.getPhotoId()));//attachService.attachDTO() orqali rasmni URL VA ID sini jonatadi
        if (!GeneralStatus.IN_REGISTRATION.equals(entity.getStatus())
                && !GeneralStatus.BLOCK.equals(entity.getStatus())
                && GeneralStatus.IN_CHECKING.equals(entity.getStatus())) {
            postDTO.setStatus(GeneralStatus.ACTIVE);
        }
        return postDTO;
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////too much use
    // postni PostEntity databasedan idsi orqali olish  YOKI   UPDATE paytda ham ishlatsak boladi
    public PostEntity get(String id) {
        return postRepository.findById(id).orElseThrow(() -> new AppBadException("Post not found: " + id));
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////too much use

}
