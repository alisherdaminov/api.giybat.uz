package api.giybat.uz.services.image;

import api.giybat.uz.dto.profile.image.AttachDTO;
import api.giybat.uz.entity.AttachEntity;
import api.giybat.uz.exceptions.AppBadException;
import api.giybat.uz.repository.image.AttachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttachService {

    @Value("${attach.upload.folder}")
    private String folderName;
    @Value("${attach.upload.url}")
    private String attachUrl;
    @Autowired
    private AttachRepository attachRepository;

    public AttachDTO upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppBadException("File not found!");
        }
        try {
            String pathFolder = getYmDString(); // 2025/01/01
            String keyUUID = UUID.randomUUID().toString(); // njnjnu7798gyr5r7686ghhj
            String extension = getExtension(file.getOriginalFilename());// .png, .jpg, .jpeg

            // create file if not exists
            File folder = new File(folderName + "/" + pathFolder);
            if (!folder.exists()) { //      / images     /2025/01/01
                boolean t = folder.mkdirs();
            }

            // Save to system
            byte[] bytes = file.getBytes();
            Path path = Paths.get(folderName + "/" + pathFolder + "/" + keyUUID + "." + extension);
            //                         / images     /2025/01/01         /njnjnu7798gyr5r7686ghhj  .png
            Files.write(path, bytes);

            // Save to DB
            AttachEntity attachEntity = new AttachEntity();
            attachEntity.setId(keyUUID + "." + extension);
            attachEntity.setPath(pathFolder);
            attachEntity.setExtension(extension);
            attachEntity.setOriginalName(file.getOriginalFilename());
            attachEntity.setSize(file.getSize());
            attachEntity.setVisible(true);
            attachRepository.save(attachEntity);
            return toDTO(attachEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<Resource> download(String id) {
        AttachEntity entity = getEntity(id);
        Path filePath = Paths.get(getPath(entity)).normalize();
        Resource resource = null;
        try {
            // resource sifatida oqib olish
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("File not found!");
            }
            // contentType qanday tipligini oqiymiz
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean updatePhoto(String id) {
        AttachEntity entity = getEntity(id);
        //attachRepository.delete(entity); -> DB dan o'chirib tashlaydi
        attachRepository.updatePhotoId(id);
        File file = new File(getPath(entity));
        boolean b = false;
        if (file.exists()) {
            b = file.delete();
        }
        return b;
    }

    public String getYmDString() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DATE);
        return year + "/" + month + "/" + day;
    }

    //getExtension-> mazgi.png degan function
    public String getExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        return fileName.substring(lastIndex + 1);
    }

    // id ni olish
    public AttachEntity getEntity(String id) {
        Optional<AttachEntity> optional = attachRepository.findById(id);
        if (optional.isEmpty()) {
            throw new AppBadException("File not found!");
        }
        return optional.get();
    }

    // /images/2025/01/01/njnjnu7798gyr5r7686ghhj.png
    // qayta qaytadan chiqarish urnga bita qilb funga oldik
    public String getPath(AttachEntity entity) {
        return folderName + "/" + entity.getPath() + "/" + entity.getId();
    }

    // qayta qaytadan chiqarish urnga bita qilb funga oldik
    // DB SAVE UCHUN
    public AttachDTO toDTO(AttachEntity attachEntity) {
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setPhotoId(attachEntity.getId());
        attachDTO.setOrigenName(attachEntity.getOriginalName());
        attachDTO.setExtension(attachEntity.getExtension());
        attachDTO.setSize(attachEntity.getSize());
        attachDTO.setUrl(openUrl(attachEntity.getId()));
        attachDTO.setCreatedDate(attachEntity.getCreatedDate());
        return attachDTO;
    }

    // for profile photo sent to user as a photo id and url while login
    public AttachDTO attachDTO(String photoId) {
        if (photoId == null) return null;
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setPhotoId(photoId);
        attachDTO.setUrl(openUrl(photoId));
        return attachDTO;
    }

    public String openUrl(String fileName) {
        return attachUrl + "/" + "download/" + fileName;//http://localhost:8080/attach/download/fa9f6adf-056d-4470-b65f-5b00dff00156.JPG

    }


}
