package store.cookshoong.www.cookshoongbackend.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.menu.repository.menu.MenuRepository;
import store.cookshoong.www.cookshoongbackend.menu.repository.menu.MenuStatusRepository;

/**
 * 관리자가 가맹점의 메뉴를 관리.
 * 메뉴 추가, 수정, 삭제.
 *
 * @author papel
 * @since 2023.07.11
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuStatusRepository menuStatusRepository;
}
