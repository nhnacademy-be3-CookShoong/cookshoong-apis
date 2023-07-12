package store.cookshoong.www.cookshoongbackend.account.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.store.exception.store.StoreValidException;
import store.cookshoong.www.cookshoongbackend.store.model.request.CreateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.store.service.StoreService;

/**
 * 사업자 회원을 위한 컨트롤러.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts/{accountId}")
public class BusinessAccountController {
    private final StoreService storeService;

    /**
     * 사업자 회원 : 매장 리스트 조회, 페이지로 구현.
     *
     * @param accountId 회원 아이디
     * @param pageable  페이지 정보
     * @return 200, 매장 리스트(페이지 별)
     */
    @GetMapping("/stores")
    public ResponseEntity<Page<SelectAllStoresResponseDto>> getStores(@PathVariable("accountId") Long accountId,
                                                                         Pageable pageable) {
        return ResponseEntity
            .ok(storeService.selectAllStores(accountId, pageable));
    }

    /**
     * 사업자 : 해당 매장 조회.
     *
     * @param storeId 매장 아이디
     * @return 매장 정보 반환
     */
    @GetMapping("/stores/{storeId}")
    public ResponseEntity<SelectStoreResponseDto> getStoreForUser(@PathVariable("accountId") Long accountId,
                                                           @PathVariable("storeId") Long storeId) {
        return ResponseEntity
            .ok(storeService.selectStore(accountId, storeId));
    }


    /**
     * 사업자 회원 : 매장 등록을 위한 컨트롤러.
     *
     * @param accountId          the account id
     * @param registerRequestDto 매장 등록을 위한 Request Body
     * @param bindingResult      validation 결과
     * @return 201 response entity
     */
    @PostMapping("/stores")
    public ResponseEntity<Void> postStore(@PathVariable("accountId") Long accountId,
                                          @RequestBody @Valid CreateStoreRequestDto registerRequestDto,
                                          BindingResult bindingResult) {
        //TODO 1. 회원정보 어디서 가져와서 넣어줘야함.

        if (bindingResult.hasErrors()) {
            throw new StoreValidException(bindingResult);
        }
        //TODO 9. status -> create로 바꾸고 안에 url 작성해야함. (추후)
        storeService.createStore(accountId, registerRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }
}
