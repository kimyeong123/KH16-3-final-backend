package com.kh.final3.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.final3.dto.SanctionDto;
import com.kh.final3.service.SanctionService;
// import com.kh.final3.error.TargetNotfoundException; // 이 예외는 Service에서 던져지고 Advice에서 처리되므로, Controller에서는 직접 사용할 필요 없음
import com.kh.final3.vo.PageVO;

@CrossOrigin
@RestController
@RequestMapping("/sanction")
public class SanctionRestController {

    @Autowired
    private SanctionService sanctionService;

    // // 1. 제재 등록
    @PostMapping("/impose")
    public ResponseEntity<String> imposeSanction(@RequestBody SanctionDto dto) {
        // // memberNo가 Long 객체이므로 null 체크 후 longValue()로 변환
        if (dto.getMemberNo() == null || dto.getType() == null || dto.getReason() == null) {
            return ResponseEntity.badRequest().body("필수 정보가 누락되었습니다.");
        }
        
        sanctionService.imposeSanction(
            dto.getMemberNo(),
            dto.getType(),
            dto.getDurationDay() != null ? dto.getDurationDay() : 0, 
            dto.getReason()
        );

        return ResponseEntity.ok("제재 등록 완료");
    }

    // // 2. 제재 해제 (PATCH와 PUT 통합 관리)
    @PutMapping("/release/{sanctionNo}")
    public void releaseSanction(@PathVariable long sanctionNo) {
        sanctionService.releaseSanction(sanctionNo);
    }
    
    @PatchMapping("/release/{sanctionNo}")
    public void release(@PathVariable long sanctionNo) {
        sanctionService.releaseSanction(sanctionNo);
    }

    // // 3. 특정 회원의 활성 제재 목록
    @GetMapping("/active/{memberNo}")
    public List<SanctionDto> getActive(@PathVariable long memberNo) {
        return sanctionService.getActiveSanctions(memberNo);
    }
    
    // // 4. 전체 제재 이력 (페이징)
    @GetMapping("/history")
    public PageVO<SanctionDto> getSanctionHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        PageVO<SanctionDto> pageVO = new PageVO<>();
        pageVO.setPage(page);
        pageVO.setSize(size); 

        return sanctionService.getSanctionHistoryWithPaging(pageVO);
    }
}