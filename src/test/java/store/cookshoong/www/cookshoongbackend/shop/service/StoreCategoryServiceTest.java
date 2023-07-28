package store.cookshoong.www.cookshoongbackend.shop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllCategoriesResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllMerchantsResponseDto;
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
