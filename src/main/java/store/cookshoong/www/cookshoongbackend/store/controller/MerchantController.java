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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.store.model.request.MerchantRegisterRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.MerchantSelectResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.MerchantUpdateResponseDto;
import store.cookshoong.www.cookshoongbackend.store.service.MerchantService;

/**
 * 가맹점 컨트롤러 구현.
 * 가맹점을 관리하는 관리자 페이지에 사용될 것.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@RestController
@RequestMapping("/api/stores/merchants")
@RequiredArgsConstructor
public class MerchantController {
    private final MerchantService merchantService;

    /**
     * 가맹점 리스트 조회를 위한 컨트롤러 구현.
     *
     * @param pageable the pageable
     * @return the response entity
     */
    @GetMapping
    public ResponseEntity<Page<MerchantSelectResponseDto>> searchMerchantList(Pageable pageable) {
        return ResponseEntity
            .ok(merchantService.getMerchantList(pageable));
    }

    /**
     * 가맹점 등록을 위한 컨트롤러.
     *
     * @param merchantRegisterRequestDto the merchant register request dto
     * @param bindingResult              the binding result
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<Void> registerMerchant(@RequestBody @Valid MerchantRegisterRequestDto merchantRegisterRequestDto,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException();
        }
        merchantService.saveMerchant(merchantRegisterRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 해당 가맹점 이름 수정을 위한 컨트롤러.
     *
     * @param merchantRegisterRequestDto the merchant register request dto
     * @param bindingResult              the binding result
     * @param merchantId                 the merchant id
     * @return the response entity
     */
    @PatchMapping("/{merchantId}")
    public ResponseEntity<Void> modifyMerchant(@RequestBody @Valid MerchantUpdateResponseDto merchantUpdateResponseDto,
                                               BindingResult bindingResult,
                                               @PathVariable("merchantId") Long merchantId) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException();
        }
        merchantService.updateMerchant(merchantId, merchantUpdateResponseDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 관리자 : 가맹점 삭제를 위한 컨트롤러.
     *
     * @param merchantId 가맹점 id
     * @return 204
     */
    @DeleteMapping("/{merchantId}")
    public ResponseEntity<Void> removeStore(@PathVariable("merchantId") Long merchantId) {
        merchantService.deleteMerchant(merchantId);
        return ResponseEntity
            .noContent().build();
    }
}
