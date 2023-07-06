package store.cookshoong.www.cookshoongbackend.store.controller;

import javax.validation.Valid;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.store.model.request.StoreRegisterRequestDto;
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
     * 매장 등록을 위한 컨트롤러.
     *
     * @param registerRequestDto 매장 등록을 위한 Request Body
     * @param bindingResult      validation 결과
     * @return 201
     */
    @PostMapping
    public ResponseEntity<Void> postStore(@RequestParam("accountId") Long accountId,
                                          @RequestBody @Valid StoreRegisterRequestDto registerRequestDto,
                                          BindingResult bindingResult) {
        //TODO 1. 회원정보 어디서 가져와서 넣어줘야함.
        //TODO 2. 주소는 어떻게 가져와서 쓰나. 주소 테이블에서 어떻게 검색해서 가져와서 써야할지

        if (bindingResult.hasErrors()) {
            throw new ValidationException();
        }
        //TODO 9. ResponseEntity 수정 (Store 조회 페이지 구현 후 다시 작성)
        storeService.createStore(accountId, registerRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    //TODO 4. 수정이 아니라 추가 정보로 영업일, 휴무일을 넣을 수 있도록 하는건?

    /**
     * 매장 삭제를 위한 컨트롤러.
     *
     * @param storeId 매장 아이디
     * @return 204
     */
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable("storeId") Long storeId) {
        storeService.removeStore(storeId);
        return ResponseEntity.noContent().build();
    }
}