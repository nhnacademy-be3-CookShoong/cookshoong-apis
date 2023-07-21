package store.cookshoong.www.cookshoongbackend.shop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresNotOutedResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreService;

/**
 * 고객을 위한 매장 Controller.
 *
 * @author jeongjewan
 * @since 2023.07.15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts/customer/{addressId}")
public class StoreSearchController {

    private final StoreService storeService;

    /**
     * 일반 유저 : 3km 이내에 위치란 매장들을 보여주는 조회 페이지.
     *
     * @param addressId 주소 아이디
     * @param pageable  매장 페이지
     * @return 상태코드 200(Ok)와 함께 응답을 반환 & 클라이언트에게 매장에 대한 정보 페이지로 반환
     */
    @GetMapping("/stores")
    public ResponseEntity<Page<SelectAllStoresNotOutedResponseDto>> getNotOutedStores(
        @PathVariable("addressId") Long addressId,
        Pageable pageable) {

        Page<SelectAllStoresNotOutedResponseDto> stores =
            storeService.selectAllStoresNotOutedResponsePage(addressId, pageable);

        return ResponseEntity.ok(stores);
    }
}
