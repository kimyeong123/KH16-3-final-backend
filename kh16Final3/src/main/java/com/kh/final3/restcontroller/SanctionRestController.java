package com.kh.final3.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.final3.service.SanctionService;
import com.kh.final3.dto.SanctionDto;
// import com.kh.final3.error.TargetNotfoundException; // 이 예외는 Service에서 던져지고 Advice에서 처리되므로, Controller에서는 직접 사용할 필요 없음
import com.kh.final3.vo.PageVO;

@CrossOrigin
@RestController
@RequestMapping("/sanction")
public class SanctionRestController {

    @Autowired
    private SanctionService sanctionService;

    /**
     * 1. 제재 등록 (POST) - 관리자 기능
     * POST /rest/sanction/impose
     * @RequestBody로 SanctionDto를 받아 Service에 전달합니다.
     */
    @PostMapping("/impose")
    public ResponseEntity<String> imposeSanction(@RequestBody SanctionDto dto) {
        
        // 필수 값 검증
        if (dto.getMemberNo() == null || dto.getType() == null || dto.getReason() == null) {
            return ResponseEntity.badRequest().body("필수 정보(회원번호, 제재 유형, 사유)가 누락되었습니다.");
        }
        
        // Service 호출 (Service의 반환 타입이 void로 변경되었다고 가정)
        sanctionService.imposeSanction(
            dto.getMemberNo(),
            dto.getType(),
            // durationDay가 null일 경우 0으로 처리 (영구/경고 처리)
            dto.getDurationDay() != null ? dto.getDurationDay() : 0, 
            dto.getReason()
        );

        return ResponseEntity.ok("제재 등록 및 처리 완료");
    }

    /**
     * 2. 제재 해제 (PUT) - 관리자 기능
     * PUT /rest/sanction/release/{sanctionNo}
     */
    @PutMapping("/release/{sanctionNo}")
    public ResponseEntity<String> releaseSanction(@PathVariable long sanctionNo) {
        
        // Service 호출 (Service의 반환 타입이 void로 변경되었으며, 실패 시 TargetNotfoundException을 던진다고 가정)
        sanctionService.releaseSanction(sanctionNo); 
        
        // 💡 Service 호출이 예외 없이 성공했다면, 성공(200 OK) 응답 반환
        return ResponseEntity.ok("제재 기록 해제 및 회원 상태 정상 복구 완료");
    }

    /**
     * 3. 전체 제재 기록 목록 조회 (GET) - 관리자 페이지 - 페이징 적용
     * GET /rest/sanction/history?page=1&size=10
     */
    @GetMapping("/history")
    public ResponseEntity<PageVO<SanctionDto>> getSanctionHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // PageVO 객체 생성 및 페이지 정보 설정
        PageVO<SanctionDto> pageVO = new PageVO<>();
        pageVO.setPage(page);
        pageVO.setSize(size); 

        PageVO<SanctionDto> result = sanctionService.getSanctionHistoryWithPaging(pageVO);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 4. 특정 제재 기록 상세 조회 (GET)
     * GET /rest/sanction/{sanctionNo}
     */
    @GetMapping("/{sanctionNo}")
    public ResponseEntity<SanctionDto> getSanctionDetail(@PathVariable long sanctionNo) {
        
        SanctionDto detail = sanctionService.getSanctionDetail(sanctionNo);

        if (detail != null) {
            return ResponseEntity.ok(detail);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}