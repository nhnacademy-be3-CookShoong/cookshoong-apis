package store.cookshoong.www.cookshoongbackend.menu_order.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateOptionGroupRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.option.MenuHasOptionGroupRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.option.OptionGroupRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 옵션 그룹 관리 서비스.
 *
 * @author papel
 * @since 2023.07.11
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OptionGroupService {
    private final OptionGroupRepository optionGroupRepository;
    private final MenuHasOptionGroupRepository menuHasOptionGroupRepository;
    private final StoreRepository storeRepository;

    /**
     * 옵션 그룹 등록 서비스.
     *
     * @param storeId                     매장 아이디
     * @param createOptionGroupRequestDto 옵션 그룹 등록을 위한 정보
     */
    public void createOptionGroup(Long storeId, CreateOptionGroupRequestDto createOptionGroupRequestDto) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);
        OptionGroup optionGroup =
            new OptionGroup(
                store,
                createOptionGroupRequestDto.getName(),
                createOptionGroupRequestDto.getMinSelectCount(),
                createOptionGroupRequestDto.getMaxSelectCount(),
                false);
        optionGroupRepository.save(optionGroup);
    }
}