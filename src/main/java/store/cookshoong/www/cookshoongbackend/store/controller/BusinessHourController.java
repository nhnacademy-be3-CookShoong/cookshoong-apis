package store.cookshoong.www.cookshoongbackend.store.controller;

import javax.validation.Valid;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.store.model.request.CreateHolidayRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.HolidayResponseDto;
import store.cookshoong.www.cookshoongbackend.store.service.BusinessHourService;

/**
 * 매장 휴업일, 영업시간 컨트롤러 구현
 *
 * @author papel
 * @since 2023.07.07
 */
@RestController
@RequestMapping("/api/stores/holiday")
@RequiredArgsConstructor
public class BusinessHourController {
    private final BusinessHourService businessHourService;


    /**
     *  리스트 조회를 위한 컨트롤러 구현.
     *
     * @param storeId  the store id
     * @param pageable the pageable
     * @return 200
     */
    @GetMapping
    public ResponseEntity<Page<HolidayResponseDto>> getHolidayList(Long storeId, Pageable pageable) {
        return ResponseEntity
            .ok(businessHourService.selectHolidayList(storeId, pageable));
    }

    /**
     *  휴업일 등록을 위한 컨트롤러 구현.
     *
     * @param storeId  the store id
     * @param createHolidayRequestDto 휴업일 등록을 위한 Request Body
     * @param bindingResult validation 결과
     * @return 201
     */
    @PostMapping
    public ResponseEntity<Void> postHoliday(@RequestParam("storeId") Long storeId,
                                            @RequestBody @Valid CreateHolidayRequestDto createHolidayRequestDto,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException();
        }

        businessHourService.createHoliday(storeId, createHolidayRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 휴업일 삭제를 위한 컨트롤러 구현.
     *
     * @param holidayId 휴일 아이디
     * @return 204
     */
    @DeleteMapping("/{holidayId}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable("holidayId") Long holidayId) {
        businessHourService.removeHoliday(holidayId);
        return ResponseEntity.noContent().build();
    }


}
