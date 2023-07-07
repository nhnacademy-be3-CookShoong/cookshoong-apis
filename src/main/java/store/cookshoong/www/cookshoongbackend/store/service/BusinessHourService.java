package store.cookshoong.www.cookshoongbackend.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.store.entity.Holiday;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;
import store.cookshoong.www.cookshoongbackend.store.model.request.CreateHolidayRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.HolidayResponseDto;
import store.cookshoong.www.cookshoongbackend.store.repository.HolidayRepository;
import store.cookshoong.www.cookshoongbackend.store.repository.StoreRepository;

/**
 * 관리자가 가맹점의 휴업일, 영업시간을 관리.
 * 휴업일 추가, 휴업일 삭제, 영업시간 추가, 영업시간 삭제.
 *
 * @author papel
 * @since 2023.07.07
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BusinessHourService {
    private final HolidayRepository holidayRepository;

    private final StoreRepository storeRepository;

    /**
     * 휴업일 리스트 조회를 위한 서비스 구현.
     *
     * @param storeId 매장 아이디
     * @param pageable 페이지 정보
     * @return 휴업일 리스트
     */
    @Transactional(readOnly = true)
    public Page<HolidayResponseDto> selectHolidayList(Long storeId, Pageable pageable) {
        return holidayRepository.lookupHolidayPage(storeId, pageable);
    }

    /**
     * 휴업일 생성을 위한 서비스 구현.
     *
     * @param storeId 매장 아이디
     * @param createHolidayRequestDto 휴업일 정보
     */
    public void createHoliday(Long storeId, CreateHolidayRequestDto createHolidayRequestDto) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다."));
        holidayRepository.save(new Holiday(store, createHolidayRequestDto.getHolidayDate()));
    }

    /**
     * 휴업일 삭제를 위한 서비스 구현.
     *
     * @param holidayId 휴업일 아이디
     */
    public void removeHoliday(Long holidayId) {
        if(!holidayRepository.existsById(holidayId)) {
            throw new IllegalArgumentException("존재하지 않는 휴업일입니다.");
        }
        holidayRepository.deleteById(holidayId);
    }

}
