package store.cookshoong.www.cookshoongbackend.payment.controller;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.payment.exception.TypeValidationException;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreateTypeRequestDto;
import store.cookshoong.www.cookshoongbackend.payment.model.request.ModifyTypeRequestDto;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TypeResponseDto;
import store.cookshoong.www.cookshoongbackend.payment.service.RefundTypeService;

/**
 * RefundType 에 대한 Controller.
 *
 * @author jeongjewan
 * @since 2023.07.06
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class RefundTypeController {

    private final RefundTypeService refundTypeService;

    /**
     * 환불 타입을 만드는 생성 메서드.
     *
     * @param requestDto    타입에 대한 name Dto
     * @return              상태코드 201(CREATED)와 함께 응답을 반환
     */
    @PostMapping("/refunds")
    public ResponseEntity<CreateTypeRequestDto> postCreateRefundType(
        @RequestBody @Valid CreateTypeRequestDto requestDto,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new TypeValidationException(bindingResult);
        }

        refundTypeService.createRefundType(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(requestDto);
    }

    /**
     * 환불 타입에 name 을 변경하는 메서드.
     *
     * @param refundTypeId  환불 타입 아이디
     * @param requestDto    환불 타입 name 에 Dto
     * @return              상태코드 200(Ok)와 함께 응답을 반환
     */
    @PutMapping("/refunds/{refundTypeId}")
    public ResponseEntity<ModifyTypeRequestDto> putModifyRefundType(@PathVariable("refundTypeId") String refundTypeId,
                                                                    @RequestBody @Valid ModifyTypeRequestDto requestDto,
                                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new TypeValidationException(bindingResult);
        }

        refundTypeService.modifyRefundType(refundTypeId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(requestDto);
    }

    /**
     * 해당 아이디에 해당하는 환불 타입을 조회하는 메서드.
     *
     * @param refundTypeId  환불 타입 아이디
     * @return              상태코드 200(Ok)와 함께 응답을 반환
     */
    @GetMapping("/refunds/{refundTypeId}")
    public ResponseEntity<TypeResponseDto> getRefundType(@PathVariable("refundTypeId") String refundTypeId) {

        TypeResponseDto chargeType = refundTypeService.selectRefundType(refundTypeId);

        return ResponseEntity.status(HttpStatus.OK).body(chargeType);
    }

    /**
     * 모든 환불 타입을 조회하는 메서드.
     *
     * @return              상태코드 200(Ok)와 함께 응답을 반환
     */
    @GetMapping("/refunds")
    public ResponseEntity<List<TypeResponseDto>> getRefundTypeAll() {

        List<TypeResponseDto> chargeTypeList = refundTypeService.selectRefundTypeAll();

        return ResponseEntity.status(HttpStatus.OK).body(chargeTypeList);
    }

    /**
     * 해당 아이디에 환불 타입을 삭제하는 메서드.
     *
     * @param refundTypeId  환불 타입 아이디
     * @return              상태코드 204(No_CONTENT)와 함께 응답을 반환
     */
    @DeleteMapping("/refunds/{refundTypeId}")
    public ResponseEntity<Void> deleteRefundType(@PathVariable("refundTypeId") String refundTypeId) {

        refundTypeService.removeRefundType(refundTypeId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
