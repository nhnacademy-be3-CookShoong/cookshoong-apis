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
}
