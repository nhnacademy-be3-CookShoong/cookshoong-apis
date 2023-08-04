package store.cookshoong.www.cookshoongbackend.shop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreCategory;
import store.cookshoong.www.cookshoongbackend.shop.exception.category.DuplicatedStoreCategoryException;
import store.cookshoong.www.cookshoongbackend.shop.exception.category.StoreCategoryNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateStoreCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllCategoriesResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.category.StoreCategoryRepository;

/**
 * 관리자 : 카테고리 조회, 생성, 수정 관련 서비스 코드 작성.
 * 사업자 : 은행 조회 코드 서비스 작성.
 *
 * @author seungyeon
 * @since 2023.07.21
 */
@ExtendWith(MockitoExtension.class)
class StoreCategoryServiceTest {
    @Mock
    private StoreCategoryRepository storeCategoryRepository;

    @InjectMocks
    private StoreCategoryService storeCategoryService;

    @Test
    void selectAllCategories() {
        // Given
        Pageable pageable = PageRequest.of(0, 4);
        List<SelectAllCategoriesResponseDto> selectAllCategoriesResponseDtos = List.of(
            new SelectAllCategoriesResponseDto("CHK", "치킨"),
            new SelectAllCategoriesResponseDto("PIZ", "피자"),
            new SelectAllCategoriesResponseDto("DES", "디저트"),
            new SelectAllCategoriesResponseDto("POR", "족발과 보쌈"),
            new SelectAllCategoriesResponseDto("BOX", "도시락")
        );
        Page<SelectAllCategoriesResponseDto> extectedPage
            = new PageImpl<>(selectAllCategoriesResponseDtos, pageable, selectAllCategoriesResponseDtos.size());
        when(storeCategoryRepository.lookupStoreCategoriesPage(pageable)).thenReturn(extectedPage);

        //when
        Page<SelectAllCategoriesResponseDto> resultPages = storeCategoryService.selectAllCategories(pageable);

        //then
        assertThat(resultPages.getContent()).isEqualTo(extectedPage.getContent());
        assertThat(resultPages.getTotalElements()).isEqualTo(extectedPage.getTotalElements());

        verify(storeCategoryRepository, times(1)).lookupStoreCategoriesPage(any(Pageable.class));
    }

    @Test
    @DisplayName("매장 카테고리 추가 - 성공")
    void createStoreCategory() {
        CreateStoreCategoryRequestDto storeCategory = ReflectionUtils.newInstance(CreateStoreCategoryRequestDto.class);
        ReflectionTestUtils.setField(storeCategory, "storeCategoryCode", "CHK");
        ReflectionTestUtils.setField(storeCategory, "storeCategoryName", "치킨");

        when(storeCategoryRepository.existsById(storeCategory.getStoreCategoryCode())).thenReturn(false);
        when(storeCategoryRepository.existsByDescription(storeCategory.getStoreCategoryName())).thenReturn(false);

        storeCategoryService.createStoreCategory(storeCategory);

        verify(storeCategoryRepository, times(1)).existsById(storeCategory.getStoreCategoryCode());
        verify(storeCategoryRepository, times(1)).existsByDescription(storeCategory.getStoreCategoryName());
        verify(storeCategoryRepository, times(1)).save(any(StoreCategory.class));
    }

    @Test
    @DisplayName("카테고리 수정 - 성공")
    void updateStoreCategory() {
        String categoryCode = "CHK";
        UpdateStoreCategoryRequestDto updateCategoryDto = ReflectionUtils.newInstance(UpdateStoreCategoryRequestDto.class);
        ReflectionTestUtils.setField(updateCategoryDto, "storeCategoryName", "치킨");

        StoreCategory storeCategory = new StoreCategory("CHK", "NONE");

        when(storeCategoryRepository.findById(categoryCode)).thenReturn(Optional.of(storeCategory));
        when(storeCategoryRepository.existsByDescription(updateCategoryDto.getStoreCategoryName())).thenReturn(false);

        storeCategoryService.updateStoreCategory(categoryCode, updateCategoryDto);

        verify(storeCategoryRepository, times(1)).findById(categoryCode);
        verify(storeCategoryRepository, times(1)).existsByDescription(updateCategoryDto.getStoreCategoryName());

        assertThat(storeCategory.getDescription()).isEqualTo(updateCategoryDto.getStoreCategoryName());
    }

    @Test
    @DisplayName("카테고리 수정 - 실패 : 이미 존재하는 카테고리명")
    void updateStoreCategory_fail_duplicated() {
        String categoryCode = "CHK";
        UpdateStoreCategoryRequestDto updateCategoryDto = ReflectionUtils.newInstance(UpdateStoreCategoryRequestDto.class);
        ReflectionTestUtils.setField(updateCategoryDto, "storeCategoryName", "치킨");

        StoreCategory storeCategory = new StoreCategory("CHK", "NONE");

        when(storeCategoryRepository.findById(categoryCode)).thenReturn(Optional.of(storeCategory));
        when(storeCategoryRepository.existsByDescription(updateCategoryDto.getStoreCategoryName())).thenReturn(true);

        assertThatThrownBy(
            () -> storeCategoryService.updateStoreCategory(categoryCode, updateCategoryDto))
            .isInstanceOf(DuplicatedStoreCategoryException.class);

        verify(storeCategoryRepository, times(1)).findById(categoryCode);
        verify(storeCategoryRepository, times(1)).existsByDescription(updateCategoryDto.getStoreCategoryName());

        assertThat(storeCategory.getDescription()).isNotEqualTo(updateCategoryDto.getStoreCategoryName());
    }

    @Test
    @DisplayName("카테고리 수정 - 실패 : 존재하지 않는 카테고리")
    void updateStoreCategory_fail_notFound() {
        String categoryCode = "NONE";
        UpdateStoreCategoryRequestDto updateCategoryDto = ReflectionUtils.newInstance(UpdateStoreCategoryRequestDto.class);
        ReflectionTestUtils.setField(updateCategoryDto, "storeCategoryName", "치킨");

        StoreCategory storeCategory = new StoreCategory("CHK", "NONE");

        when(storeCategoryRepository.findById(categoryCode)).thenReturn(Optional.empty());

        assertThatThrownBy(
            () -> storeCategoryService.updateStoreCategory(categoryCode, updateCategoryDto))
            .isInstanceOf(StoreCategoryNotFoundException.class);

        verify(storeCategoryRepository, times(1)).findById(categoryCode);
        verify(storeCategoryRepository, times(0)).existsByDescription(updateCategoryDto.getStoreCategoryName());

        assertThat(storeCategory.getDescription()).isNotEqualTo(updateCategoryDto.getStoreCategoryName());
    }

    @Test
    @DisplayName("카테고리 리스트 조회 - 성공")
    void selectAllCategoriesForUser() {
        // Given
        List<SelectAllCategoriesResponseDto> selectAllCategoriesResponseDtos = List.of(
            new SelectAllCategoriesResponseDto("BOX", "도시락"),
            new SelectAllCategoriesResponseDto("CHK", "치킨"),
            new SelectAllCategoriesResponseDto("DES", "디저트"),
            new SelectAllCategoriesResponseDto("PIZ", "피자"),
            new SelectAllCategoriesResponseDto("POR", "족발과 보쌈")
        );
        when(storeCategoryRepository.findAllByOrderByCategoryCodeAsc()).thenReturn(selectAllCategoriesResponseDtos);

        // When
        List<SelectAllCategoriesResponseDto> result = storeCategoryService.selectAllCategoriesForUser();

        // Then
        assertThat(result).isEqualTo(selectAllCategoriesResponseDtos);

        verify(storeCategoryRepository, times(1)).findAllByOrderByCategoryCodeAsc();
    }
}
