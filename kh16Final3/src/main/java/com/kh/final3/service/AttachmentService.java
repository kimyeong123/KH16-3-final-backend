package com.kh.final3.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
    private static final String UPLOAD_ROOT = "final3_uploads";
//영도님 board쓰시면돼요 
    public AttachmentDto saveProduct(MultipartFile file, long productNo) throws IOException {
        return saveInternal(file, "PRODUCT", productNo);
    }

    public AttachmentDto saveBoard(MultipartFile file, long boardNo) throws IOException {
        return saveInternal(file, "BOARD", boardNo);
    }


    public AttachmentDto saveQna(MultipartFile file, long qnaNo) throws IOException {
        return saveInternal(file, "BOARD", qnaNo); 
    }

    public AttachmentDto save(MultipartFile file, String category, long parentPkNo) throws IOException {
        return saveInternal(file, category, parentPkNo);
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

        // 1) PK
        int seq = attachmentDao.sequence();
        long attachmentNo = seq;

        // 2) 디렉토리 준비: {home}/final3_uploads/{category}
        File categoryDir = new File(home, UPLOAD_ROOT + File.separator + category);
        if (!categoryDir.exists()) categoryDir.mkdirs();

        // 3) 파일명
        String originalName = file.getOriginalFilename();
        if (originalName == null) originalName = "unknown";

        String storedName = category + "_" + attachmentNo + "_" + originalName;

        // 4) 실제 파일 저장
        File target = new File(categoryDir, storedName);

        // DB path는 상대경로로 저장
        String relativePath = UPLOAD_ROOT + "/" + category;

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
        }
        catch (Exception e) {
            if (target.exists()) target.delete();
            throw new RuntimeException("첨부 저장 실패(파일/DB). 파일은 롤백 처리됨", e);
        }
    }

    public ByteArrayResource load(int attachmentNo) throws IOException {
        AttachmentDto dto = attachmentDao.selectOne(attachmentNo);
        if (dto == null) throw new IllegalArgumentException("존재하지 않는 첨부파일");

        File target = buildTargetFile(dto.getPath(), dto.getStoredName());
        if (!target.isFile()) throw new IllegalArgumentException("실제 파일이 존재하지 않음");

        byte[] data = Files.readAllBytes(target.toPath());
        return new ByteArrayResource(data);
    }

    public AttachmentDto get(int attachmentNo) {
        AttachmentDto dto = attachmentDao.selectOne(attachmentNo);
        if (dto == null) throw new IllegalArgumentException("존재하지 않는 첨부파일");
        return dto;
    }

    public java.util.List<AttachmentDto> listByParent(String category, int parentPkNo) {
        return attachmentDao.selectListByParent(category.trim().toUpperCase(), parentPkNo);
    }

    @Transactional
    public boolean delete(int attachmentNo) {
        AttachmentDto dto = attachmentDao.selectOne(attachmentNo);
        if (dto == null) throw new IllegalArgumentException("존재하지 않는 첨부파일");

        File target = buildTargetFile(dto.getPath(), dto.getStoredName());
        if (!target.isFile()) throw new IllegalArgumentException("실제 파일이 존재하지 않음");

        boolean fileDeleted = target.delete();
        if (!fileDeleted) throw new RuntimeException("파일 삭제 실패(사용중/잠금 가능성). DB 롤백");

        return attachmentDao.delete(attachmentNo);
    }

    @Transactional
    public boolean deleteByParent(String category, int parentPkNo) {

        var list = attachmentDao.selectListByParent(category.trim().toUpperCase(), parentPkNo);

        for (AttachmentDto dto : list) {
            File target = buildTargetFile(dto.getPath(), dto.getStoredName());
            if (target.isFile()) {
                boolean ok = target.delete();
                if (!ok) throw new RuntimeException("파일 삭제 실패로 중단. DB 롤백");
            }
        }

        return attachmentDao.deleteByParent(category.trim().toUpperCase(), parentPkNo);
    }

    private File buildTargetFile(String path, String storedName) {
        File parentDir = new File(home, path);
        return new File(parentDir, storedName);
    }
}
