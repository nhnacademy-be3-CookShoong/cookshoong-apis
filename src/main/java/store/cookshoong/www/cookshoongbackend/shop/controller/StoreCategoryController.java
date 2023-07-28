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
import store.cookshoong.www.cookshoongbackend.shop.exception.category.StoreCategoryValidException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateStoreCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllCategoriesResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreCategoryService;

/**
 * 관리자가 관리할 매장 카테고리 페이지.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class StoreCategoryController {
    private final StoreCategoryService storeCategoryService;

    /**
     * 관리자 : 매장 카테고리 리스트 조회.
     * 추후에 세밀하게 카테고리가 나누게 될 것을 고려하여 페이징 처리함.
     *
     * @param pageable 페이지정보
     * @return 200, 카테고리 리스트
     */
    @GetMapping
    public ResponseEntity<Page<SelectAllCategoriesResponseDto>> getStoreCategories(Pageable pageable) {
        return ResponseEntity
            .ok(storeCategoryService.selectAllCategories(pageable));
    }

    /**
     * 관리자 : 새로운 매장 카테고리 등록.
     *
     * @param requestDto    the request dto
     * @param bindingResult the binding result
     * @return 201 response entity
     */
    @PostMapping
    public ResponseEntity<Void> postStoreCategory(@RequestBody @Valid CreateStoreCategoryRequestDto requestDto,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new StoreCategoryValidException(bindingResult);
        }

        storeCategoryService.createStoreCategory(requestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 관리자 : 매장 카테고리 수정.
     *
     * @param requestDto    매장 카테고리 이름
     * @param bindingResult binding 결과
     * @param categoryCode  카테고리 코드
     * @return 200 response entity
     */
    @PatchMapping("/{categoryCode}")
    public ResponseEntity<Void> patchStoreCategory(@RequestBody @Valid UpdateStoreCategoryRequestDto requestDto,
                                                   BindingResult bindingResult,
                                                   @PathVariable("categoryCode") String categoryCode) {
        if (bindingResult.hasErrors()) {
            throw new StoreCategoryValidException(bindingResult);
        }
        storeCategoryService.updateStoreCategory(categoryCode, requestDto);
        return ResponseEntity
            .ok()
            .build();
    }

    /**
     * 관리자 : 매장 카테고리 삭제.
     *
     * @param categoryCode 카테고리 코드
     * @return 204
     */
    @DeleteMapping("/{categoryCode}")
    public ResponseEntity<Void> patchStoreCategory(@PathVariable("categoryCode") String categoryCode) {
        storeCategoryService.removeCategory(categoryCode);
        return ResponseEntity
            .noContent()
            .build();
    }
}
