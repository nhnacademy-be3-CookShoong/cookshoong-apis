package store.cookshoong.www.cookshoongbackend.store.controller;

import javax.validation.Valid;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.store.model.request.StoreRegisterRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.MerchantResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.StoreListResponseDto;
import store.cookshoong.www.cookshoongbackend.store.service.StoreService;

/**
 * 매장관련 컨트롤러 구현.
 *
 * @author seungyeon
 * @since 2023.07.05
 */
@RequestMapping("/api/stores")
@RestController
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    /**
     * 사업자 회원의 매장 리스트 조회, 페이지로 구현.
     *
     * @param accountId 회원 아이디
     * @param pageable  페이지 정보
     * @return 페이지 별 응답 엔티티
     */
    @GetMapping("/list/{accountId}")
    public ResponseEntity<Page<StoreListResponseDto>> searchStoreList(@PathVariable("accountId") Long accountId,
                                                                      Pageable pageable) {
        return ResponseEntity
            .ok(storeService.getStoreList(accountId, pageable));
    }

    /**
     * Register store response entity.
     *
     * @param registerRequestDto the register request dto
     * @param bindingResult      the binding result
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<Void> registerStore(@RequestBody @Valid StoreRegisterRequestDto registerRequestDto,
                                              BindingResult bindingResult) {
        //TODO 1. 회원정보 어디서 가져와서 넣어줘야함.
        //TODO 2. 주소는 어떻게 가져와서 쓰나. 주소 테이블에서 어떻게 검색해서 가져와서 써야할지

        if (bindingResult.hasErrors()) {
            throw new ValidationException();
        }
        //TODO 9. ResponseEntity 수정 (Store 조회 페이지 구현 후 다시 작성)
        storeService.saveStore(registerRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }
    //TODO 4. 수정이 아니라 추가 정보로 영업일, 휴무일을 넣을 수 있도록 하는건?

    /**
     * Remove store response entity.
     *
     * @param storeId the store id
     * @return the response entity
     */
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> removeStore(@PathVariable("storeId") Long storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.noContent().build();
    }
}
