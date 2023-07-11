package store.cookshoong.www.cookshoongbackend.store.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.store.model.request.CreateBankRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllBanksResponseDto;
import store.cookshoong.www.cookshoongbackend.store.service.BankTypeService;

/**
 * 관리자 : 은행 타입 추가.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/banks")
public class BankController {
    private final BankTypeService bankTypeService;

    /**
     * 은행 리스트 조회.
     *
     * @param pageable page 정보
     * @return 페이지별 은행리스트
     */
    @GetMapping
    public ResponseEntity<Page<SelectAllBanksResponseDto>> getBanks(Pageable pageable) {
        return ResponseEntity
            .ok(bankTypeService.selectBanks(pageable));
    }

    /**
     * 은행 등록.
     *
     * @param requestDto 은행 등록 request
     * @return 201
     */
    @PostMapping
    public ResponseEntity<Void> postBank(@RequestBody @Valid CreateBankRequestDto requestDto) {
        // TODO 7.추후 Created관련 상태 변경
        bankTypeService.createBank(requestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }
}
