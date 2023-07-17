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
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionValidationException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateOptionRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.model.response.SelectOptionResponseDto;
import store.cookshoong.www.cookshoongbackend.menu_order.service.OptionService;

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

    /**
     * 일반 유저 : 매장의 메뉴들을 보여주는 조회 페이지.
     *
     * @param storeId 주소 아이디
     * @return          상태코드 200(Ok)와 함께 응답을 반환 & 클라이언트에게 매장의 옵션 리스트로 반환
     */
    @GetMapping("/option")
    public ResponseEntity<List<SelectOptionResponseDto>> getOptions(@PathVariable("storeId") Long storeId) {
        List<SelectOptionResponseDto> options = optionService.selectOptions(storeId);
        return ResponseEntity.ok(options);
    }
}
