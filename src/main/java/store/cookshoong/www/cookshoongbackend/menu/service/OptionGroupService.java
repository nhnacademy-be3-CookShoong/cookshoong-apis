package store.cookshoong.www.cookshoongbackend.menu.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.menu.repository.optiongroup.MenuHasOptionGroupRepository;
import store.cookshoong.www.cookshoongbackend.menu.repository.optiongroup.OptionGroupRepository;

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

}
