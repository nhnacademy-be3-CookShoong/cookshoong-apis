package store.cookshoong.www.cookshoongbackend.menu.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.menu.repository.menugroup.MenuGroupRepository;
import store.cookshoong.www.cookshoongbackend.menu.repository.menugroup.MenuHasMenuGroupRepository;

/**
 * 관리자가 가맹점의 메뉴 그룹을 관리.
 * 메뉴 그룹 추가, 수정, 삭제.
 *
 * @author papel
 * @since 2023.07.11
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MenuGroupService {
    private final MenuGroupRepository menuGroupRepository;
    private final MenuHasMenuGroupRepository menuHasMenuGroupRepository;
}
