package store.cookshoong.www.cookshoongbackend.shop.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.stauts.StoreStatusRepository;

/**
 * 매장 상태관련 service 구현.
 *
 * @author seungyeon
 * @since 2023.07.16
 */
@Service
@RequiredArgsConstructor
public class StoreStatusService {
    private final StoreStatusRepository storeStatusRepository;

    public List<SelectAllStatusResponseDto> selectAllStatusForUser(){
        return storeStatusRepository.findAllBy();
    }
}
