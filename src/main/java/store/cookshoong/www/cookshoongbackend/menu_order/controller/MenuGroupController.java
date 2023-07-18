package store.cookshoong.www.cookshoongbackend.menu_order.controller;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuGroupValidationException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateMenuGroupRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuGroupResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.service.MenuGroupService;

/**
 * 메뉴 그룹 컨트롤러.
 *
 * @author papel
 * @since 2023.07.13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuGroupController {
    private final MenuGroupService menuGroupService;

    /**
     * 메뉴 그룹 등록 컨트롤러.
     *
     * @param storeId                   매장 아이디
     * @param createMenuGroupRequestDto 메뉴 등록 Dto
     * @param bindingResult             validation
     * @return 201 response
     */
    @PostMapping("/stores/{storeId}/menu-group")
    public ResponseEntity<Void> postMenuGroup(@PathVariable("storeId") Long storeId,
                                         @RequestBody @Valid CreateMenuGroupRequestDto createMenuGroupRequestDto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MenuGroupValidationException(bindingResult);
        }

        menuGroupService.createMenuGroup(storeId, createMenuGroupRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 메뉴 그룹 리스트 조회 컨트롤러.
     *
     * @param storeId 매장 아이디
     * @return 200 response, 메뉴 그룹 리스트
     */
    @GetMapping("/stores/{storeId}/menu-group")
    public ResponseEntity<List<SelectMenuGroupResponseDto>> getMenuGroups(@PathVariable("storeId") Long storeId) {
        List<SelectMenuGroupResponseDto> menuGroups = menuGroupService.selectMenuGroups(storeId);
        return ResponseEntity.ok(menuGroups);
    }

    /**
     * 메뉴 그룹 조회 컨트롤러.
     *
     * @param menuGroupId 메뉴 그룹 아이디
     * @return 200 response, 메뉴 그룹
     */
    @GetMapping("/menu-group/{menuGroupId}")
    public ResponseEntity<SelectMenuGroupResponseDto> getMenuGroup(@PathVariable("menuGroupId") Long menuGroupId) {
        SelectMenuGroupResponseDto menuGroup = menuGroupService.selectMenuGroup(menuGroupId);
        return ResponseEntity.ok(menuGroup);
    }
}
