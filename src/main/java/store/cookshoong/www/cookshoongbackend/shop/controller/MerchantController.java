package store.cookshoong.www.cookshoongbackend.shop.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.shop.exception.merchant.MerchantValidException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateMerchantRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateMerchantRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllMerchantsResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.MerchantService;

/**
 * 가맹점 컨트롤러 구현.
 * 가맹점을 관리하는 관리자 페이지에 사용될 것.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@RestController
@RequestMapping("/api/admin/merchants")
@RequiredArgsConstructor
public class MerchantController {
    private final MerchantService merchantService;

    /**
     * 관리자 : 가맹점 리스트 조회를 위한 컨트롤러 구현.
     *
     * @param pageable the pageable
     * @return the response entity
     */
    @GetMapping
    public ResponseEntity<Page<SelectAllMerchantsResponseDto>> getMerchants(Pageable pageable) {
        return ResponseEntity
            .ok(merchantService.selectAllMerchants(pageable));
    }

    /**
     * 관리자 : 가맹점 등록을 위한 컨트롤러.
     *
     * @param createMerchantRequestDto 가맹점 등록 정보
     * @param bindingResult            valid 결과
     * @return 201
     */
    @PostMapping
    public ResponseEntity<Void> postMerchant(@RequestBody @Valid CreateMerchantRequestDto createMerchantRequestDto,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MerchantValidException(bindingResult);
        }
        merchantService.createMerchant(createMerchantRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 관리자 : 해당 가맹점 이름 수정을 위한 컨트롤러.
     *
     * @param updateMerchantRequestDto 가맹점 수정을 위한 정보
     * @param bindingResult            valid 결과
     * @param merchantId               가맹점 id
     * @return 200
     */
    @PatchMapping("/{merchantId}")
    public ResponseEntity<Void> patchMerchant(@RequestBody @Valid UpdateMerchantRequestDto updateMerchantRequestDto,
                                              BindingResult bindingResult,
                                              @PathVariable("merchantId") Long merchantId) {
        if (bindingResult.hasErrors()) {
            throw new MerchantValidException(bindingResult);
        }
        merchantService.updateMerchant(merchantId, updateMerchantRequestDto);
        return ResponseEntity
            .ok()
            .build();
    }

    /**
     * 관리자 : 가맹점 삭제를 위한 컨트롤러.
     *
     * @param merchantId 가맹점 id
     * @return 204
     */
    @DeleteMapping("/{merchantId}")
    public ResponseEntity<Void> deleteStore(@PathVariable("merchantId") Long merchantId) {
        merchantService.removeMerchant(merchantId);
        return ResponseEntity
            .noContent()
            .build();
    }
}
