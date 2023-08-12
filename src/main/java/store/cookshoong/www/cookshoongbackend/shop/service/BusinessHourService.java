package store.cookshoong.www.cookshoongbackend.shop.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.shop.entity.BusinessHour;
import store.cookshoong.www.cookshoongbackend.shop.entity.DayType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Holiday;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.exception.businesshour.DayTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateBusinessHourRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateHolidayRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectBusinessHourResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectHolidayResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.businesshour.BusinessHourRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.businesshour.DayTypeRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.businesshour.HolidayRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.stauts.StoreStatusRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 관리자가 가맹점의 휴업일, 영업시간을 관리.
 * 휴업일 추가, 휴업일 삭제, 영업시간 추가, 영업시간 삭제.
 *
 * @author papel (윤동현)
 * @contributor seungyeon (유승연)
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
    private final StoreStatusRepository storeStatusRepository;

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
     * 영업시간 리스트 조회를 위한 서비스 구현.
     *
     * @param storeId 매장 아이디
     * @return 영업시간 리스트
     */
    @Transactional(readOnly = true)
    public List<SelectBusinessHourResponseDto> selectBusinessHours(Long storeId) {
        return businessHourRepository.lookupBusinessHours(storeId);
    }

    /**
     * 휴업일 리스트 조회를 위한 서비스 구현.
     *
     * @param storeId 매장 아이디
     * @return 휴업일 리스트
     */
    @Transactional(readOnly = true)
    public List<SelectHolidayResponseDto> selectHolidays(Long storeId) {
        return holidayRepository.lookupHolidays(storeId);
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
     * 영업시간 삭제를 위한 서비스 구현.
     *
     * @param businessHourId 휴업일 아이디
     */
    public void removeBusinessHour(Long businessHourId) {
        businessHourRepository.deleteById(businessHourId);
    }

    /**
     * 휴무일과 영업시간을 기준으로 매장 상태 변경하기.
     *
     * @param storeId the store id
     * @return the boolean
     */
    public Store updateStoreStatusByTimer(Long storeId) { // 처음에 이걸 가져다 쓸 때 CLOSE가 아닐 때 해당 메소드가 실행이 되도록 조건을 걸어주세요.
        Store store = getStoreById(storeId);

        if (holidayRepository.lookupHolidayByStoreId(storeId, LocalDate.now())) {
            store.modifyStoreStatus(storeStatusRepository.getReferenceById(StoreStatus.StoreStatusCode.BREAK_TIME.name()));
            return store;
        }
        String status = StoreStatus.StoreStatusCode.BREAK_TIME.name();
        if (businessHourRepository.lookupBusinessHourByDayCode(storeId, LocalDate.now().getDayOfWeek().name().substring(0, 3), LocalTime.now())) {
            status = StoreStatus.StoreStatusCode.OPEN.name();
        }

        store.modifyStoreStatus(storeStatusRepository.getReferenceById(status));
        return store;
    }

    private Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);
    }
}
