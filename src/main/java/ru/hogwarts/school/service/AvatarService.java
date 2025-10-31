package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.EntityNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${avatar.dir.path:avatars}")
    private String avatarDir;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar saveAvatar(Long studentId, MultipartFile file) throws IOException {
        Student student = studentRepository.findById(studentId).orElseThrow(() ->
                new EntityNotFoundException("Студент с id=" + studentId + " не найден"));

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElse(new Avatar());

        String fileName = studentId + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(avatarDir).resolve(fileName);

        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        return avatarRepository.save(avatar);
    }

    public Avatar getAvatarFromDB(Long studentId) {
        return avatarRepository.findByStudentId(studentId).orElseThrow(() ->
                new EntityNotFoundException("Аватар для студента с id=" + studentId + " не найден"));
    }

    public byte[] getAvatarFromFile(Long studentId) throws IOException {
        Avatar avatar = getAvatarFromDB(studentId);
        Path path = Paths.get(avatar.getFilePath());
        if (!Files.exists(path)) {
            throw new EntityNotFoundException("Файл аватара не найден по пути: " + avatar.getFilePath());
        }
        return Files.readAllBytes(path);
    }

    public Page<Avatar> getAvatars(Pageable pageable) {
        return avatarRepository.findAll(pageable);
    }
}