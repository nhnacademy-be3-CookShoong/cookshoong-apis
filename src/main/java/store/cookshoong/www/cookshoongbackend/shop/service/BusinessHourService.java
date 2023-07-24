package store.cookshoong.www.cookshoongbackend.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.shop.entity.BusinessHour;
import store.cookshoong.www.cookshoongbackend.shop.entity.DayType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Holiday;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.exception.businesshour.DayTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateBusinessHourRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateHolidayRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectBusinessHourResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectHolidayResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.businesshour.BusinessHourRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.businesshour.DayTypeRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.businesshour.HolidayRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 관리자가 가맹점의 휴업일, 영업시간을 관리.
 * 휴업일 추가, 휴업일 삭제, 영업시간 추가, 영업시간 삭제.
 *
 * @author papel (윤동현)
 * @since 2023.07.07
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BusinessHourService {
    private final HolidayRepository holidayRepository;

    private final BusinessHourRepository businessHourRepository;

    private final DayTypeRepository dayTypeRepository;

    private final StoreRepository storeRepository;

    /**
     * 휴업일 리스트 조회를 위한 서비스 구현.
     *
     * @param storeId  매장 아이디
     * @param pageable 페이지 정보
     * @return 휴업일 리스트
     */
    @Transactional(readOnly = true)
    public Page<SelectHolidayResponseDto> selectHolidayPage(Long storeId, Pageable pageable) {
        return holidayRepository.lookupHolidayPage(storeId, pageable);
    }

    /**
     * 휴업일 생성을 위한 서비스 구현.
     *
     * @param storeId                 매장 아이디
     * @param createHolidayRequestDto 휴업일 정보
     */
    public void createHoliday(Long storeId, CreateHolidayRequestDto createHolidayRequestDto) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);
        Holiday holiday = new Holiday(store,
            createHolidayRequestDto.getHolidayStartDate(),
            createHolidayRequestDto.getHolidayEndDate());
        holidayRepository.save(holiday);
    }

    /**
     * 휴업일 삭제를 위한 서비스 구현.
     *
     * @param holidayId 휴업일 아이디
     */
    public void removeHoliday(Long holidayId) {
        holidayRepository.deleteById(holidayId);
    }


    /**
     * 영업시간 리스트 조회를 위한 서비스 구현.
     *
     * @param storeId  매장 아이디
     * @param pageable 페이지 정보
     * @return 영업시간 리스트
     */
    @Transactional(readOnly = true)
    public Page<SelectBusinessHourResponseDto> selectBusinessHourPage(Long storeId, Pageable pageable) {
        return businessHourRepository.lookupBusinessHourPage(storeId, pageable);
    }

    /**
     * 영업시간 생성을 위한 서비스 구현.
     *
     * @param storeId                      매장 아이디
     * @param createBusinessHourRequestDto 영업시간 정보
     */
    public void createBusinessHour(Long storeId, CreateBusinessHourRequestDto createBusinessHourRequestDto) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);
        DayType dayType = dayTypeRepository.findByDescription(createBusinessHourRequestDto.getDayCodeName())
            .orElseThrow(DayTypeNotFoundException::new);
        BusinessHour businessHour = new BusinessHour(store,
            dayType,
            createBusinessHourRequestDto.getOpenHour(),
            createBusinessHourRequestDto.getCloseHour());

        businessHourRepository.save(businessHour);
    }

    /**
     * 영업시간 삭제를 위한 서비스 구현.
     *
     * @param businessHourId 휴업일 아이디
     */
    public void removeBusinessHour(Long businessHourId) {
        businessHourRepository.deleteById(businessHourId);
    }

}
