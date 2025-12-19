package com.kh.final3.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.final3.dao.AttachmentDao;
import com.kh.final3.dto.AttachmentDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentDao attachmentDao;
    private final File home = new File(System.getProperty("user.home"));
    private static final String UPLOAD_ROOT_DIR_NAME = "final3_uploads"; 
    private final File uploadRoot = new File(home, UPLOAD_ROOT_DIR_NAME); 

    public AttachmentDto saveProduct(MultipartFile file, long productNo) throws IOException {
        return saveInternal(file, "PRODUCT", productNo);
    }
    public AttachmentDto saveBoard(MultipartFile file, long boardNo) throws IOException {
        return saveInternal(file, "BOARD", boardNo);
    }
    public AttachmentDto saveQna(MultipartFile file, long qnaNo) throws IOException {
        return saveInternal(file, "BOARD", qnaNo);
    }
    
    @Transactional
    private AttachmentDto saveInternal(MultipartFile file, String category, long parentPkNo) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드 파일이 비어있습니다");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("category가 비어있습니다");
        }

        category = category.trim().toUpperCase();
        long seq = attachmentDao.sequence();
        long attachmentNo = seq;

        File categoryDir = new File(uploadRoot, category); 
        if (!categoryDir.exists()) categoryDir.mkdirs(); 

        String originalName = file.getOriginalFilename();
        if (originalName == null) originalName = "unknown";

        String storedName = category + "_" + attachmentNo + "_" + originalName;
        File target = new File(categoryDir, storedName);
        String relativePath = UPLOAD_ROOT_DIR_NAME + File.separator + category; 

        try {
            file.transferTo(target);
            AttachmentDto dto = AttachmentDto.builder()
                    .attachmentNo(attachmentNo)
                    .mediaType(file.getContentType())
                    .path(relativePath)
                    .category(category)
                    .parentPkNo(parentPkNo)
                    .originalName(originalName)
                    .storedName(storedName)
                    .fileSize(file.getSize())
                    .build();

            attachmentDao.insert(dto);
            return dto;
        } catch (Exception e) {
            if (target.exists()) target.delete();
            throw new RuntimeException("첨부 저장 실패", e); 
        }
    }

    public ByteArrayResource load(long attachmentNo) throws IOException {
        AttachmentDto dto = attachmentDao.selectOne(attachmentNo);
        if (dto == null) throw new IllegalArgumentException("존재하지 않는 첨부파일");

        File target = buildTargetFile(dto.getPath(), dto.getStoredName());
        // [수정] 파일이 없으면 예외를 던져서 Controller에서 처리하게 함
        if (!target.exists() || !target.isFile()) {
            throw new IOException("파일을 찾을 수 없습니다: " + target.getAbsolutePath());
        }

        byte[] data = Files.readAllBytes(target.toPath());
        return new ByteArrayResource(data);
    }

    public AttachmentDto get(long attachmentNo) {
        AttachmentDto dto = attachmentDao.selectOne(attachmentNo);
        if (dto == null) throw new IllegalArgumentException("존재하지 않는 첨부파일");
        return dto;
    }

    public List<AttachmentDto> listByParent(String category, long parentPkNo) {
        return attachmentDao.selectListByParent(category.trim().toUpperCase(), parentPkNo);
    }

    @Transactional
    public boolean delete(long attachmentNo) {
        AttachmentDto dto = attachmentDao.selectOne(attachmentNo);
        if (dto == null) return false;

        File target = buildTargetFile(dto.getPath(), dto.getStoredName());
        // [수정] 파일이 존재할 때만 삭제 시도, 실패해도 DB 삭제는 진행
        if (target.exists() && target.isFile()) {
            target.delete();
        }
        return attachmentDao.delete(attachmentNo);
    }

    @Transactional
    public boolean deleteByParent(String category, long parentPkNo) {
        var list = attachmentDao.selectListByParent(category.trim().toUpperCase(), parentPkNo);

        for (AttachmentDto dto : list) {
            File target = buildTargetFile(dto.getPath(), dto.getStoredName());
            // [수정] 파일 존재 여부 확인 후 삭제 (예외 방지)
            if (target.exists() && target.isFile()) {
                target.delete();
            }
        }
        // 파일 삭제 성공 여부와 상관없이 DB 데이터는 지움
        return attachmentDao.deleteByParent(category.trim().toUpperCase(), parentPkNo);
    }

    private File buildTargetFile(String path, String storedName) {
        File parentDir = new File(home, path);
        return new File(parentDir, storedName);
    }

    @Transactional 
    public void save(long parentPkNo, List<MultipartFile> files, String category) {
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    try {
                        saveInternal(file, category, parentPkNo);
                    } catch (IOException e) {
                        throw new RuntimeException("첨부파일 저장 중 오류", e);
                    }
                }
            }
        }
    }
    
    public Long findProductThumbnailAttachmentNo(long productNo) {
        return attachmentDao.findFirstAttachmentNoByProduct(productNo);
    }
}