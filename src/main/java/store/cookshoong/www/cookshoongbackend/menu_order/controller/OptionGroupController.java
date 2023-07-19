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
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionGroupValidationException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateOptionGroupRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.service.OptionGroupService;

/**
 * 옵션 그룹 컨트롤러.
 *
 * @author papel
 * @since 2023.07.13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores/{storeId}")
public class OptionGroupController {
    private final OptionGroupService optionGroupService;

    /**
     * 옵션 그룹 등록 컨트롤러.
     *
     * @param storeId                     매장 아이디
     * @param createOptionGroupRequestDto 옵션 그룹 등록 Dto
     * @param bindingResult               validation
     * @return 201 response
     */
    @PostMapping("/option-group")
    public ResponseEntity<Void> postOption(@PathVariable("storeId") Long storeId,
                                         @RequestBody @Valid CreateOptionGroupRequestDto createOptionGroupRequestDto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new OptionGroupValidationException(bindingResult);
        }

        optionGroupService.createOptionGroup(storeId, createOptionGroupRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }
}