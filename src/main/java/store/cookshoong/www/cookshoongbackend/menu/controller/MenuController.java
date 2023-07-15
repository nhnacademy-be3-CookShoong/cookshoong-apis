package store.cookshoong.www.cookshoongbackend.menu.controller;

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
import store.cookshoong.www.cookshoongbackend.menu.exception.menu.MenuValidationException;
import store.cookshoong.www.cookshoongbackend.menu.model.request.CreateMenuRequestDto;
import store.cookshoong.www.cookshoongbackend.menu.service.MenuService;

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
}
