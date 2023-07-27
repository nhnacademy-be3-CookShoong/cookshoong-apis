package store.cookshoong.www.cookshoongbackend.menu_order.controller;

import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionValidationException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateOptionRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.service.OptionService;

/**
 * 옵션 컨트롤러.
 *
 * @author papel (윤동현)
 * @since 2023.07.13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OptionController {
    private final OptionService optionService;

    /**
     * 옵션 등록 컨트롤러.
     *
     * @param createOptionRequestDto 옵션 등록 Dto
     * @param bindingResult          validation
     * @return 201 response
     */
    @PostMapping("/stores/{storeId}/option")
    public ResponseEntity<Void> postOption( @RequestBody @Valid CreateOptionRequestDto createOptionRequestDto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new OptionValidationException(bindingResult);
        }
        optionService.createOption(createOptionRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 옵션 리스트 조회 컨트롤러.
     *
     * @param storeId 매장 아이디
     * @return 200 response, 옵션 리스트
     */
    @GetMapping("/stores/{storeId}/option")
    public ResponseEntity<List<SelectOptionResponseDto>> getOptions(@PathVariable("storeId") Long storeId) {
        List<SelectOptionResponseDto> options = optionService.selectOptions(storeId);
        return ResponseEntity.ok(options);
    }

    /**
     * 옵션 조회 컨트롤러.
     *
     * @param optionId 옵션 아이디
     * @return 200 response, 옵션
     */
    @GetMapping("/stores/{storeId}/option/{optionId}")
    public ResponseEntity<SelectOptionResponseDto> getOption(
        @PathVariable("storeId") Long storeId,
        @PathVariable("optionId") Long optionId) {
        SelectOptionResponseDto option = optionService.selectOption(optionId);
        return ResponseEntity.ok(option);
    }

    /**
     * 옵션 삭제 컨트롤러.
     *
     * @param storeId  매장 아이디
     * @param optionId 옵션 아이디
     * @return 200 response
     */
    @DeleteMapping("/stores/{storeId}/option/{optionId}")
    public ResponseEntity<Void> deleteOption(@PathVariable("storeId") Long storeId,
                                           @PathVariable("optionId") Long optionId) {
        optionService.deleteOption(storeId, optionId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .build();
    }
}
