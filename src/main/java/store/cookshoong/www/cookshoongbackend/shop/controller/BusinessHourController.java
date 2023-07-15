package store.cookshoong.www.cookshoongbackend.shop.controller;

import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.shop.exception.HolidayValidationException;
import store.cookshoong.www.cookshoongbackend.shop.exception.businesshour.BusinessHourValidationException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateBusinessHourRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateHolidayRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectBusinessHourResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectHolidayResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.BusinessHourService;

/**
 * 매장 휴업일, 영업시간 컨트롤러 구현.
 *
 * @author papel
 * @since 2023.07.07
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stores/{storeId}")
public class BusinessHourController {
    private final BusinessHourService businessHourService;

    /**
     * 휴업일 리스트 조회를 위한 컨트롤러 구현.
     *
     * @param storeId  the store id
     * @param pageable the pageable
     * @return 200, 휴업일 페이지
     */
    @GetMapping("/holiday")
    public ResponseEntity<Page<SelectHolidayResponseDto>> getHolidayPage(@PathVariable("storeId") Long storeId,
                                                                         Pageable pageable) {

        return ResponseEntity
            .ok(businessHourService.selectHolidayPage(storeId, pageable));
    }

    /**
     * 영업시간 리스트 조회를 위한 컨트롤러 구현.
     *
     * @param storeId  the store id
     * @param pageable the pageable
     * @return 200
     */
    @GetMapping("/businesshour")
    public ResponseEntity<Page<SelectBusinessHourResponseDto>> getBusinessHourPage(@PathVariable("storeId") Long storeId,
                                                                                   Pageable pageable) {
        return ResponseEntity
            .ok(businessHourService.selectBusinessHourPage(storeId, pageable));
    }

    /**
     * 휴업일 등록을 위한 컨트롤러 구현.
     *
     * @param storeId                 the store id
     * @param createHolidayRequestDto 휴업일 등록을 위한 Request Body
     * @param bindingResult           validation 결과
     * @return 201
     */
    @PostMapping("/holiday")
    public ResponseEntity<Void> postHoliday(@PathVariable("storeId") Long storeId,
                                            @RequestBody @Valid CreateHolidayRequestDto createHolidayRequestDto,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new HolidayValidationException(bindingResult);
        }

        businessHourService.createHoliday(storeId, createHolidayRequestDto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .build();
    }

    /**
     * 영업시간 등록을 위한 컨트롤러 구현.
     *
     * @param storeId                      the store id
     * @param createBusinessHourRequestDto 영업시간 등록을 위한 Request Body
     * @param bindingResult                validation 결과
     * @return 201
     */
    @PostMapping("/businesshour")
    public ResponseEntity<Void> postBusinessHour(@PathVariable("storeId") Long storeId,
                                                 @RequestBody @Valid CreateBusinessHourRequestDto createBusinessHourRequestDto,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BusinessHourValidationException(bindingResult);
        }

        businessHourService.createBusinessHour(storeId, createBusinessHourRequestDto);
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
    @DeleteMapping("/holiday/{holidayId}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable("holidayId") Long holidayId) {
        businessHourService.removeHoliday(holidayId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 영업시간 삭제를 위한 컨트롤러 구현.
     *
     * @param businesshourId 영업시간 아이디
     * @return 204
     */
    @DeleteMapping("/businesshour/{businesshourId}")
    public ResponseEntity<Void> deleteBusinessHour(@PathVariable("businesshourId") Long businesshourId) {
        businessHourService.removeBusinessHour(businesshourId);
        return ResponseEntity.noContent().build();
    }
}
