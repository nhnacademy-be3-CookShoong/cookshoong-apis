package store.cookshoong.www.cookshoongbackend.menu_order.service;


import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionGroupNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateOptionGroupRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.option.OptionGroupRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 옵션 그룹 관리 서비스.
 *
 * @author papel (윤동현)
 * @since 2023.07.11
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OptionGroupService {
    private final OptionGroupRepository optionGroupRepository;
    private final StoreRepository storeRepository;

    /**
     * 옵션 그룹 등록 서비스.
     *
     * @param storeId               매장 아이디
     * @param createOptionGroupRequestDto 옵션 그룹 등록 Dto
     */
    public void updateOptionGroup(Long storeId, CreateOptionGroupRequestDto createOptionGroupRequestDto) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);
        if (Objects.isNull(createOptionGroupRequestDto.getTargetOptionGroupId())) {
            OptionGroup optionGroup =
                new OptionGroup(
                    store,
                    createOptionGroupRequestDto.getName(),
                    createOptionGroupRequestDto.getMinSelectCount(),
                    createOptionGroupRequestDto.getMaxSelectCount(),
                    false);
            optionGroupRepository.save(optionGroup);
        } else {
            OptionGroup optionGroup = optionGroupRepository.findById(createOptionGroupRequestDto.getTargetOptionGroupId())
                .orElseThrow(OptionGroupNotFoundException::new);
            optionGroup.modifyOptionGroup(createOptionGroupRequestDto);
        }
    }

    /**
     * 옵션 그룹 리스트 조회 서비스.
     *
     * @param storeId 매장 아이디
     * @return 매장의 옵션 그룹 리스트
     */
    public List<SelectOptionGroupResponseDto> selectOptionGroups(Long storeId) {
        return optionGroupRepository.lookupOptionGroups(storeId);
    }

    /**
     * 옵션 그룹 조회 서비스.
     *
     * @param optionGroupId 옵션 그룹 아이디
     * @return 매장의 옵션 그룹
     */
    public SelectOptionGroupResponseDto selectOptionGroup(Long optionGroupId) {
        return optionGroupRepository.lookupOptionGroup(optionGroupId)
            .orElseThrow(OptionGroupNotFoundException::new);
    }

    /**
     * 옵션 그룹 삭제 서비스.
     *
     * @param storeId       매장 아이디
     * @param optionGroupId 옵션 그룹 아이디
     */
    public void deleteOptionGroup(Long storeId, Long optionGroupId) {
        OptionGroup optionGroup = optionGroupRepository.findById(optionGroupId)
            .orElseThrow(OptionGroupNotFoundException::new);
        optionGroup.modifyOptionGroupIsDeleted(true);
    }
}
