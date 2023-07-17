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
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuValidationException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateMenuRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.service.MenuService;

/**
 * 메뉴 컨트롤러 구현.
 *
 * @author papel
 * @since 2023.07.13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores/{storeId}")
public class MenuController {
    private final MenuService menuService;

    /**
     * 메뉴 등록을 위한 컨트롤러 구현.
     *
     * @param storeId                 the store id
     * @param createMenuRequestDto    메뉴 등록을 위한 Request Body
     * @param bindingResult           validation 결과
     * @return 201
     */
    @PostMapping("/menu")
    public ResponseEntity<Void> postMenu(@PathVariable("storeId") Long storeId,
                                            @RequestBody @Valid CreateMenuRequestDto createMenuRequestDto,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new MenuValidationException(bindingResult);
        }

        menuService.createMenu(storeId, createMenuRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 일반 유저 : 매장의 메뉴들을 보여주는 조회 페이지.
     *
     * @param storeId 주소 아이디
     * @return          상태코드 200(Ok)와 함께 응답을 반환 & 클라이언트에게 매장의 메뉴 리스트로 반환
     */
    @GetMapping("/menu")
    public ResponseEntity<List<SelectMenuResponseDto>> getMenus(@PathVariable("storeId") Long storeId) {
        List<SelectMenuResponseDto> menus = menuService.selectMenus(storeId);
        return ResponseEntity.ok(menus);
    }
}
