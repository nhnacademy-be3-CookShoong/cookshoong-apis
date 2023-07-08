package store.cookshoong.www.cookshoongbackend.store.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.store.exception.store.StoreValidException;
import store.cookshoong.www.cookshoongbackend.store.model.request.CreateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectStoreForUserResponseDto;
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

    //TODO 5. 일반 회원 입장에서 OPEN, 혹은 CLOSE된 매장들만 볼 수 있도록 해야함.

    /**
     * 일반 유저 : 매장 정보 조회 페이지.
     *
     * @param storeId 매장 아이디
     * @return 매장 정보
     */
    @GetMapping("/{storeId}/info")
    public ResponseEntity<SelectStoreForUserResponseDto> getStore(@PathVariable("storeId") Long storeId) {
        return ResponseEntity.ok(storeService.selectStoreForUser(storeId));
    }


    /**
     * 사업자 회원 : 매장 등록을 위한 컨트롤러.
     *
     * @param accountId          the account id
     * @param registerRequestDto 매장 등록을 위한 Request Body
     * @param bindingResult      validation 결과
     * @return 201 response entity
     */
    @PostMapping
    public ResponseEntity<Void> postStore(@RequestParam("accountId") Long accountId,
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

    //TODO 4. 수정이 아니라 추가 정보로 영업일, 휴무일을 넣을 수 있도록 하는건?

    //TODO 8. 매장 삭제가 아니라 상태를 폐업으로 바꾸도록
}
