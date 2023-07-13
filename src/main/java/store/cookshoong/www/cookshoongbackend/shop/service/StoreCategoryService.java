package store.cookshoong.www.cookshoongbackend.shop.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreCategory;
import store.cookshoong.www.cookshoongbackend.shop.exception.category.DuplicatedStoreCategoryException;
import store.cookshoong.www.cookshoongbackend.shop.exception.category.StoreCategoryNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateStoreCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllCategoriesForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllCategoriesResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.category.StoreCategoryRepository;

/**
 * 관리자가 관리할 매장 카테고리.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StoreCategoryService {
    private final StoreCategoryRepository storeCategoryRepository;

    private void duplicatedCategoryCode(String code) {
        if (storeCategoryRepository.existsById(code)) {
            throw new DuplicatedStoreCategoryException(code);
        }
    }

    private void duplicatedStoreCategory(String categoryName) {
        if (storeCategoryRepository.existsByDescription(categoryName)) {
            throw new DuplicatedStoreCategoryException(categoryName);
        }
    }

    /**
     * 관리자 : 모든 매장 카테고리 조회.
     * 추후에 세밀하게 카테고리로 나뉘게 되었을 때를 고려하여 페이징 처리로 구현.
     *
     * @param pageable 페이지 정보
     * @return 페이지 별
     */
    @Transactional(readOnly = true)
    public Page<SelectAllCategoriesResponseDto> selectAllCategories(Pageable pageable) {
        return storeCategoryRepository.lookupStoreCategoriesPage(pageable);
    }

    /**
     * 관리자 : 매장 카테고리 생성.
     *
     * @param storeCategoryRequestDto 카테고리 이름
     */
    public void createStoreCategory(CreateStoreCategoryRequestDto storeCategoryRequestDto) {
        duplicatedCategoryCode(storeCategoryRequestDto.getStoreCategoryCode());
        duplicatedStoreCategory(storeCategoryRequestDto.getStoreCategoryName());
        storeCategoryRepository.save(new StoreCategory(storeCategoryRequestDto.getStoreCategoryName()));
    }

    /**
     * 관리자 : 매장 카테괼 수정.
     *
     * @param categoryCode the category code
     * @param requestDto   카테고리 이름
     */
    public void updateStoreCategory(String categoryCode, UpdateStoreCategoryRequestDto requestDto) {
        StoreCategory storeCategory = storeCategoryRepository.findById(categoryCode)
            .orElseThrow(StoreCategoryNotFoundException::new);
        duplicatedCategoryCode(requestDto.getStoreCategoryCode());
        duplicatedStoreCategory(requestDto.getStoreCategoryName());
        storeCategory.updateStoreCategory(requestDto.getStoreCategoryName());
    }

    /**
     * 사업자 : 매장 카테고리에 대한 리스트 반환.
     *
     * @return the list
     */
    public List<SelectAllCategoriesForUserResponseDto> selectAllCategoriesForUser() {
        return storeCategoryRepository.lookupStoreCategories();
    }

    //TODO 10. 카테고리 삭제는 오늘 작성.
}
