package store.cookshoong.www.cookshoongbackend.shop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.address.model.response.AddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.repository.accountaddress.AccountAddressRepository;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.service.ObjectStorageService;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresNotOutedResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * {설명을 작성해주세요}.
 *
 * @author jeongjewan
 * @since 2023.07.14
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class StoreServiceNotOutedTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private AccountAddressRepository accountAddressRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @InjectMocks
    private StoreService storeService;

    @Test
    void selectAllStoresLatLngResponsePage() {

        Long accountId = 1L;
        String storeCategoryCode = "CHK";


        Pageable pageable = Pageable.ofSize(10).withPage(0);

        List<SelectAllStoresNotOutedResponseDto> stores = new ArrayList<>();
        stores.add(new SelectAllStoresNotOutedResponseDto(1L, "미술대", "영업중", "주소 1", "상세주소 1",
            new BigDecimal("35.14385822588584"), new BigDecimal("126.93046054250793"), storeCategoryCode, UUID.randomUUID()+".jpg"));

        stores.forEach(selectAllStoresNotOutedResponseDto -> selectAllStoresNotOutedResponseDto.setSavedName(
            objectStorageService.getFullPath(FileDomain.STORE_IMAGE.getVariable(), selectAllStoresNotOutedResponseDto.getSavedName())
        ));
        Page<SelectAllStoresNotOutedResponseDto> storePage = new PageImpl<>(stores, pageable, stores.size());
        when(storeRepository.lookupStoreLatLanPage(pageable)).thenReturn(storePage);

        AddressResponseDto address =
            new AddressResponseDto(accountId, "광주 서석동", "조선대 IT 융합대학",
                new BigDecimal("35.14003855958521"), new BigDecimal("126.93423851916953"));
        when(accountAddressRepository.lookupByAccountSelectAddressId(accountId)).thenReturn(address);

        Page<SelectAllStoresNotOutedResponseDto> result =
            storeService.selectAllStoresNotOutedResponsePage(accountId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);

        SelectAllStoresNotOutedResponseDto actual = result.getContent().get(0);

        assertEquals(actual.getMainPlace(), stores.get(0).getMainPlace());
        assertEquals(actual.getDetailPlace(), stores.get(0).getDetailPlace());
        assertEquals(actual.getStoreStatus(), stores.get(0).getStoreStatus());
        assertEquals(actual.getLatitude(), stores.get(0).getLatitude());
        assertEquals(actual.getLongitude(), stores.get(0).getLongitude());
        assertEquals(actual.getSavedName(), stores.get(0).getSavedName());
    }
}

