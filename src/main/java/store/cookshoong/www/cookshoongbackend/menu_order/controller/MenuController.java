package store.cookshoong.www.cookshoongbackend.menu_order.controller;

import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.menu.MenuValidationException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateMenuRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.service.MenuService;

/**
 * 메뉴 컨트롤러.
 *
 * @author papel (윤동현)
 * @since 2023.07.13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {
    private final MenuService menuService;

    /**
     * 메뉴 등록 컨트롤러.
     *
     * @param storeId              매장 아이디
     * @param createMenuRequestDto 메뉴 등록 Dto
     * @param bindingResult        validation
     * @return 201 response
     */
    @PostMapping("/stores/{storeId}/menu")
    public ResponseEntity<Void> postMenu(@PathVariable("storeId") Long storeId,
                                         @RequestPart("requestDto") @Valid CreateMenuRequestDto createMenuRequestDto,
                                         BindingResult bindingResult,
                                         @RequestPart(value = "menuImage", required = false) MultipartFile image,
                                         @RequestParam("storedAt") String storedAt) throws IOException {
        if (bindingResult.hasErrors()) {
            throw new MenuValidationException(bindingResult);
        }
        menuService.createMenu(storeId, createMenuRequestDto, storedAt, image);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    @PatchMapping("/stores/{storeId}/menu")
    public ResponseEntity<Void> patchMenu(@PathVariable("storeId") Long storeId,
                                          @RequestPart("requestDto") @Valid CreateMenuRequestDto createMenuRequestDto,
                                          BindingResult bindingResult,
                                          @RequestPart(value = "menuImage", required = false) MultipartFile image,
                                          @RequestParam("storedAt") String storedAt) throws IOException {
        if (bindingResult.hasErrors()) {
            throw new MenuValidationException(bindingResult);
        }
        menuService.updateMenu(createMenuRequestDto, storedAt, image);
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }

    /**
     * 메뉴 리스트 조회 컨트롤러.
     *
     * @param storeId 매장 아이디
     * @return 200 response, 메뉴 리스트
     */
    @GetMapping("/stores/{storeId}/menu")
    public ResponseEntity<List<SelectMenuResponseDto>> getMenus(@PathVariable("storeId") Long storeId) {
        List<SelectMenuResponseDto> menus = menuService.selectMenus(storeId);
        return ResponseEntity.ok(menus);
    }

    /**
     * 메뉴 조회 컨트롤러.
     *
     * @param menuId 메뉴 아이디
     * @return 200 response, 메뉴
     */
    @GetMapping("/stores/{storeId}/menu/{menuId}")
    public ResponseEntity<SelectMenuResponseDto> getMenu(
        @PathVariable("storeId") Long storeId,
        @PathVariable("menuId") Long menuId) {
        SelectMenuResponseDto menu = menuService.selectMenu(menuId);
        return ResponseEntity.ok(menu);
    }

    /**
     * 메뉴 삭제 컨트롤러.
     *
     * @param storeId 매장 아이디
     * @param menuId  메뉴 아이디
     * @return 200 response
     */
    @DeleteMapping("/stores/{storeId}/menu/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable("storeId") Long storeId,
                                           @PathVariable("menuId") Long menuId) throws IOException {
        menuService.deleteMenu(storeId, menuId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }
}
