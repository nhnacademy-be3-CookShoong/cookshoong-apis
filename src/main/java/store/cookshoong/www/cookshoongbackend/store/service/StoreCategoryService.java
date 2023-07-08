package store.cookshoong.www.cookshoongbackend.store.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.store.entity.StoreCategory;
import store.cookshoong.www.cookshoongbackend.store.exception.category.DuplicatedStoreCategoryException;
import store.cookshoong.www.cookshoongbackend.store.exception.category.StoreCategoryNotFoundException;
import store.cookshoong.www.cookshoongbackend.store.model.request.CreateStoreCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.request.UpdateStoreCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllCategoriesResponseDto;
import store.cookshoong.www.cookshoongbackend.store.repository.StoreCategoryRepository;

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
        if (storeCategoryRepository.existsByDescription(storeCategoryRequestDto.getStoreCategoryName())) {
            throw new DuplicatedStoreCategoryException(storeCategoryRequestDto.getStoreCategoryName());
        }
        storeCategoryRepository.save(new StoreCategory(storeCategoryRequestDto.getStoreCategoryName()));
    }

    /**
     * 관리자 : 매장 카테괼 수정.
     *
     * @param requestDto 카테고리 이름
     */
    public void updateStoreCategory(String categoryCode, UpdateStoreCategoryRequestDto requestDto) {
        StoreCategory storeCategory = storeCategoryRepository.findById(categoryCode)
            .orElseThrow(StoreCategoryNotFoundException::new);

        if (storeCategoryRepository.existsByDescription(requestDto.getStoreCategoryName())) {
            throw new DuplicatedStoreCategoryException(requestDto.getStoreCategoryName());
        }
        storeCategory.updateStoreCategory(requestDto.getStoreCategoryName());
    }

    //TODO 10. 카테고리 삭제는 오늘 작성.
}
