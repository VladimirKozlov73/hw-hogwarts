package ru.hogwarts.school.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping("/{studentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> uploadAvatar(@PathVariable Long studentId, @RequestParam MultipartFile file) throws IOException {
        Avatar avatar = avatarService.saveAvatar(studentId, file);
        return Map.of("id", avatar.getId());
    }

    @GetMapping("/db/{studentId}")
    public ResponseEntity<byte[]> getAvatarFromDB(@PathVariable Long studentId) {
        Avatar avatar = avatarService.getAvatarFromDB(studentId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(avatar.getFileSize()))
                .body(avatar.getData());
    }

    @GetMapping("/file/{studentId}")
    public ResponseEntity<byte[]> getAvatarFromFile(@PathVariable Long studentId) throws IOException {
        Avatar avatar = avatarService.getAvatarFromDB(studentId);
        byte[] data = avatarService.getAvatarFromFile(studentId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(avatar.getMediaType()))
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(avatar.getFileSize()))
                .body(data);
    }
}
