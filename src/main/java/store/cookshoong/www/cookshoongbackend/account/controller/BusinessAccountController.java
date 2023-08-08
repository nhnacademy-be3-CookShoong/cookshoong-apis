package store.cookshoong.www.cookshoongbackend.account.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreValidException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreStatusRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreService;

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
     * @return 200, 매장 리스트(페이지 별)
     */
    @GetMapping("/stores")
    public ResponseEntity<List<SelectAllStoresResponseDto>> getStores(@PathVariable("accountId") Long accountId) {
        return ResponseEntity
            .ok(storeService.selectAllStores(accountId));
    }

    /**
     * 사업자 : 해당 매장 조회.
     *
     * @param accountId the account id
     * @param storeId   매장 아이디
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
     * @param businessLicense    the business license
     * @param image              the image
     * @return 201 response entity
     * @throws IOException the io exception
     */
    @PostMapping("/stores")
    public ResponseEntity<Void> postStore(@PathVariable("accountId") Long accountId,
                                          @RequestPart("requestDto") @Valid CreateStoreRequestDto registerRequestDto,
                                          BindingResult bindingResult,
                                          @RequestPart("businessInfoImage") MultipartFile businessLicense,
                                          @RequestPart(name = "storeImage", required = false) MultipartFile image,
                                          @RequestParam("storedAt") String storedAt) throws IOException, URISyntaxException {

        if (bindingResult.hasErrors() || Objects.isNull(businessLicense)) {
            throw new StoreValidException(bindingResult);
        }

        Map<String, MultipartFile> fileMap = new HashMap<>();
        fileMap.put(FileDomain.BUSINESS_INFO_IMAGE.getVariable(), businessLicense);
        if (Objects.nonNull(image)) {
            fileMap.put(FileDomain.STORE_IMAGE.getVariable(), image);
        }

        Long storeId = storeService.createStore(accountId, registerRequestDto, storedAt, fileMap);
        URI uri = new URI("/stores/" + storeId + "/store-info-manager");

        return ResponseEntity
            .created(uri)
            .build();
    }


    /**
     * 매장 정보 수정 컨트롤러.
     *
     * @param storeId       the store id
     * @param accountId     the account id
     * @param requestDto    매장 정보 request
     * @param bindingResult valid 결과값
     * @return 200 response entity
     */
    @PutMapping("/stores/{storeId}")
    public ResponseEntity<Void> putStore(@PathVariable("storeId") Long storeId,
                                         @PathVariable("accountId") Long accountId,
                                         @RequestPart("requestDto") @Valid UpdateStoreRequestDto requestDto,
                                         BindingResult bindingResult,
                                         @RequestPart("storeImage") MultipartFile storeImage,
                                         @RequestParam("storedAt") String storedAt) throws IOException {
        if (bindingResult.hasErrors()) {
            throw new StoreValidException(bindingResult);
        }

        storeService.updateStore(accountId, storeId, requestDto, storedAt, storeImage);
        return ResponseEntity
            .ok()
            .build();
    }

    /**
     * 매장 카테고리 수정 컨트롤러.
     *
     * @param accountId  the account id
     * @param storeId    the store id
     * @param requestDto 매장 카테고리 code 리스트
     * @return 200 response entity
     */
    @PatchMapping("/stores/{storeId}/categories")
    public ResponseEntity<Void> patchStoreCategory(@PathVariable("accountId") Long accountId,
                                                   @PathVariable("storeId") Long storeId,
                                                   @RequestBody UpdateCategoryRequestDto requestDto) {
        storeService.updateStoreCategories(accountId, storeId, requestDto);
        return ResponseEntity
            .ok()
            .build();
    }

    /**
     * 매장 상태 변경 수정에 대한 컨트롤러.
     *
     * @param accountId  the account id
     * @param storeId    the store id
     * @param requestDto the request dto
     * @return 200
     */
    @PatchMapping("/stores/{storeId}/status")
    public ResponseEntity<Void> patchStoreStatus(@PathVariable("accountId") Long accountId,
                                                 @PathVariable("storeId") Long storeId,
                                                 @RequestBody @Valid UpdateStoreStatusRequestDto requestDto) {
        storeService.updateStoreStatus(accountId, storeId, requestDto);
        return ResponseEntity
            .ok()
            .build();
    }
}
