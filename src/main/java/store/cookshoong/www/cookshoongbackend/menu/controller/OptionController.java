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
import store.cookshoong.www.cookshoongbackend.menu.exception.option.OptionValidationException;
import store.cookshoong.www.cookshoongbackend.menu.model.request.CreateOptionRequestDto;
import store.cookshoong.www.cookshoongbackend.menu.service.OptionService;

/**
 * 옵션 컨트롤러 구현.
 *
 * @author papel
 * @since 2023.07.13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores/{storeId}")
public class OptionController {
    private final OptionService optionService;

    /**
     * 옵션 등록을 위한 컨트롤러 구현.
     *
     * @param optionGroupId             옵션 그룹 아이디
     * @param createOptionRequestDto    옵션 등록을 위한 Request Body
     * @param bindingResult             validation 결과
     * @return 201
     */
    @PostMapping("/option-group/{optionGroupId}/option")
    public ResponseEntity<Void> postOption(@PathVariable("optionGroupId") Integer optionGroupId,
                                         @RequestBody @Valid CreateOptionRequestDto createOptionRequestDto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new OptionValidationException(bindingResult);
        }

        optionService.createOption(optionGroupId, createOptionRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }
}
