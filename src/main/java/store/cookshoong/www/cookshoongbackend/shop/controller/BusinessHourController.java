package store.cookshoong.www.cookshoongbackend.shop.controller;

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
import store.cookshoong.www.cookshoongbackend.shop.exception.businesshour.BusinessHourValidationException;
import store.cookshoong.www.cookshoongbackend.shop.exception.holiday.HolidayValidationException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateBusinessHourRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateHolidayRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectBusinessHourResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectHolidayResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.BusinessHourService;

/**
 * 매장 휴업일, 영업시간 컨트롤러 구현.
 *
 * @author papel (윤동현)
 * @since 2023.07.07
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BusinessHourController {
    private final BusinessHourService businessHourService;

    /**
     * 영업시간 등록을 위한 컨트롤러 구현.
     *
     * @param storeId                      the store id
     * @param createBusinessHourRequestDto 영업시간 등록을 위한 Request Body
     * @param bindingResult                validation 결과
     * @return 201
     */
    @PostMapping("/stores/{storeId}/business-hour")
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
     * 휴업일 등록을 위한 컨트롤러 구현.
     *
     * @param storeId                 the store id
     * @param createHolidayRequestDto 휴업일 등록을 위한 Request Body
     * @param bindingResult           validation 결과
     * @return 201
     */
    @PostMapping("/stores/{storeId}/holiday")
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
     * 영업시간 리스트 조회를 위한 컨트롤러 구현.
     *
     * @param storeId the store id
     * @return 200 response, 영업시간 리스트
     */
    @GetMapping("/stores/{storeId}/business-hour")
    public ResponseEntity<List<SelectBusinessHourResponseDto>> getBusinessHours(
        @PathVariable("storeId") Long storeId) {
        List<SelectBusinessHourResponseDto> businessHours = businessHourService.selectBusinessHours(storeId);
        return ResponseEntity.ok(businessHours);
    }

    /**
     * 휴업일 리스트 조회를 위한 컨트롤러 구현.
     *
     * @param storeId the store id
     * @return 200 response, 휴업일 리스트
     */
    @GetMapping("/stores/{storeId}/holiday")
    public ResponseEntity<List<SelectHolidayResponseDto>> getHolidays(
        @PathVariable("storeId") Long storeId) {
        List<SelectHolidayResponseDto> holidays = businessHourService.selectHolidays(storeId);
        return ResponseEntity.ok(holidays);
    }


    /**
     * 영업시간 삭제를 위한 컨트롤러 구현.
     *
     * @param businesshourId 영업시간 아이디
     * @return 204
     */
    @DeleteMapping("/stores/{storeId}/business-hour/{businesshourId}")
    public ResponseEntity<Void> deleteBusinessHour(
        @PathVariable("storeId") Long storeId,
        @PathVariable("businesshourId") Long businesshourId) {
        businessHourService.removeBusinessHour(businesshourId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 휴업일 삭제를 위한 컨트롤러 구현.
     *
     * @param holidayId 휴일 아이디
     * @return 204
     */
    @DeleteMapping("/stores/{storeId}/holiday/{holidayId}")
    public ResponseEntity<Void> deleteHoliday(
        @PathVariable("storeId") Long storeId,
        @PathVariable("holidayId") Long holidayId) {
        businessHourService.removeHoliday(holidayId);
        return ResponseEntity.noContent().build();
    }

}
