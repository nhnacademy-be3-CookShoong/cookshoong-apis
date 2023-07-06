package store.cookshoong.www.cookshoongbackend.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.store.model.response.StoreListResponseDto;
import store.cookshoong.www.cookshoongbackend.store.service.StoreService;

/**
 * 사업자 회원을 위한 컨트롤러.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts/{accountId}")
public class BusinessAccountController {
    private final StoreService storeService;
    /**
     * 사업자 회원의 매장 리스트 조회, 페이지로 구현.
     *
     * @param accountId 회원 아이디
     * @param pageable  페이지 정보
     * @return 200, 매장 리스트(페이지 별)
     */
    @GetMapping("/stores")
    public ResponseEntity<Page<StoreListResponseDto>> getStoreList(@PathVariable("accountId") Long accountId,
                                                                   Pageable pageable) {
        return ResponseEntity
            .ok(storeService.selectStoreList(accountId, pageable));
    }
}
