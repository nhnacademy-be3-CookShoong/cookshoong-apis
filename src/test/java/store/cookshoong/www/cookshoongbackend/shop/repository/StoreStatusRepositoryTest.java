package store.cookshoong.www.cookshoongbackend.shop.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import store.cookshoong.www.cookshoongbackend.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.stauts.StoreStatusRepository;

/**
 * 관리자, 사업자 입장에서 볼 수 있는 매장 상태관련 리스트 조회 Test code 작성.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.21
 */
@DataJpaTest
@Import(QueryDslConfig.class)
class StoreStatusRepositoryTest {

    @Autowired
    private StoreStatusRepository storeStatusRepository;
    @Test
    @DisplayName("매장 상태를 나타내는 상태 목록 리스트 조회 - 성공")
    void find_all_store_status(){
        //Given
        StoreStatus storeOpened = new StoreStatus("OPEN", "영업중");
        StoreStatus storeClosed = new StoreStatus("CLOSE", "휴식중");
        StoreStatus storeOuted = new StoreStatus("OUTED", "폐업");

        storeStatusRepository.saveAll(List.of(storeOpened, storeClosed, storeOuted));

        //When
        List<SelectAllStatusResponseDto> result = storeStatusRepository.findAllBy();

        //Then
        assertEquals(3, result.size());

        assertEquals("OPEN", result.get(0).getStoreStatusCode());
        assertEquals("CLOSE", result.get(1).getStoreStatusCode());
        assertEquals("OUTED", result.get(2).getStoreStatusCode());

        assertEquals("영업중", result.get(0).getDescription());
        assertEquals("휴식중", result.get(1).getDescription());
        assertEquals("폐업", result.get(2).getDescription());
    }
}
