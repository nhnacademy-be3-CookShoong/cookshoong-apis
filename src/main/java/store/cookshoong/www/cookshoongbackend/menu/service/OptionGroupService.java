package store.cookshoong.www.cookshoongbackend.menu.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.menu.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.menu.model.request.CreateOptionGroupRequestDto;
import store.cookshoong.www.cookshoongbackend.menu.repository.optiongroup.MenuHasOptionGroupRepository;
import store.cookshoong.www.cookshoongbackend.menu.repository.optiongroup.OptionGroupRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.SelectStoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 관리자가 가맹점의 옵션 그룹을 관리.
 * 옵션 그룹 추가, 수정, 삭제.
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
     * 사업자 : 옵션 그룹 등록 서비스 구현.
     * 옵션 그룹 등록, 처음 상태는 삭제되지 않음으로 고정.
     *
     * @param storeId                     매장 아이디
     * @param createOptionGroupRequestDto 옵션 그룹 등록을 위한 정보
     */
    public void createOptionGroup(Long storeId, CreateOptionGroupRequestDto createOptionGroupRequestDto) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(SelectStoreNotFoundException::new);
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
