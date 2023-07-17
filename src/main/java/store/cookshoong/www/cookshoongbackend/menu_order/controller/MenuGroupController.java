package store.cookshoong.www.cookshoongbackend.menu_order.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuGroupValidationException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateMenuGroupRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.service.MenuGroupService;

/**
 * 메뉴 그룹 컨트롤러 구현.
 *
 * @author papel
 * @since 2023.07.13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores/{storeId}")
public class MenuGroupController {
    private final MenuGroupService menuGroupService;

    /**
     * 메뉴 그룹 등록을 위한 컨트롤러 구현.
     *
     * @param storeId                 the store id
     * @param createMenuGroupRequestDto    메뉴 등록을 위한 Request Body
     * @param bindingResult           validation 결과
     * @return 201
     */
    @PostMapping("/menu-group")
    public ResponseEntity<Void> postMenu(@PathVariable("storeId") Long storeId,
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
}
