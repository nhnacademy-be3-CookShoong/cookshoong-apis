package store.cookshoong.www.cookshoongbackend.shop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.service.ObjectStorageService;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreCategory;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.DuplicatedBusinessLicenseException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.bank.BankTypeRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.category.StoreCategoryRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.merchant.MerchantRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.stauts.StoreStatusRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;
import store.cookshoong.www.cookshoongbackend.util.TestPersistEntity;

/**
 * 매장 서비스 코드 테스트 작성.
 *
 * @author seungyeon (유승연)
 * @since 2023.07.21
 */
@ExtendWith(MockitoExtension.class)
class StoreServiceTest {
    @Mock
    private BankTypeRepository bankTypeRepository;
    @Mock
    private MerchantRepository merchantRepository;
    @Mock
    private ObjectStorageService objectStorageService;
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private StoreCategoryRepository storeCategoryRepository;

    @Mock
    private StoreStatusRepository storeStatusRepository;

    @InjectMocks
    private StoreService storeService;

    @Spy
    TestEntity te = new TestEntity();
    @InjectMocks
    TestPersistEntity tpe;

    Account account = te.persist(
        te.getAccount(te.getAccountStatusActive(), te.getAuthorityCustomer(), te.getRankLevelOne()));

    @Test
    @DisplayName("사장님의 매장 조회 - 리스트로 반환 - 성공")
    void selectAllStores() {
        List<SelectAllStoresResponseDto> selectAllStores = List.of(
            new SelectAllStoresResponseDto(1L, "한국음식점", "광주광역시 지산동", "1-1번지", "영업중"),
            new SelectAllStoresResponseDto(2L, "중국음식점", "광주광역시 지산동", "2-2번지", "준비중"),
            new SelectAllStoresResponseDto(3L, "태국음식점", "광주광역시 지산동", "3-3번지", "폐업")
        );


        when(storeRepository.lookupStores(account.getId())).thenReturn(selectAllStores);

        List<SelectAllStoresResponseDto> result = storeService.selectAllStores(account.getId());

        verify(storeRepository, times(1)).lookupStores(account.getId());

        assertThat(result).isEqualTo(selectAllStores);
    }

    @Test
    @DisplayName("사장님 해당 매장 조회 - 성공")
    void selectStore() {
        Store store = tpe.getOpenStoreByOneAccount(account);
        ReflectionTestUtils.setField(store, "id", 1L);
        store.modifyAddress(new Address("광주 지산동", "조선대 정문", BigDecimal.ONE, BigDecimal.ONE));

        SelectStoreResponseDto selectStore = new SelectStoreResponseDto(store.getBusinessLicenseNumber(),
            store.getRepresentativeName(),
            store.getOpeningDate(),
            store.getName(),
            store.getPhoneNumber(),
            store.getAddress().getMainPlace(),
            store.getAddress().getDetailPlace(),
            store.getAddress().getLatitude(),
            store.getAddress().getLongitude(),
            store.getDefaultEarningRate(),
            store.getDescription(),
            store.getBankTypeCode().getBankTypeCode(),
            store.getBankAccountNumber(),
            store.getStoreImage().getSavedName());

        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));

        when(storeRepository.lookupStore(account.getId(), store.getId())).thenReturn(Optional.of(selectStore));
        selectStore.setPathName(objectStorageService.getFullPath(FileDomain.STORE_IMAGE.getVariable(), selectStore.getPathName()));

        SelectStoreResponseDto result = storeService.selectStore(account.getId(), store.getId());

        assertThat(result.getStoreName()).isEqualTo(selectStore.getStoreName());
        assertThat(result.getRepresentativeName()).isEqualTo(selectStore.getRepresentativeName());
        assertThat(result.getBusinessLicenseNumber()).isEqualTo(selectStore.getBusinessLicenseNumber());
        assertThat(result.getOpeningDate()).isEqualTo(selectStore.getOpeningDate());
        assertThat(result.getBankCode()).isEqualTo(selectStore.getBankCode());
        assertThat(result.getLatitude()).isEqualTo(selectStore.getLatitude());
        assertThat(result.getLongitude()).isEqualTo(selectStore.getLongitude());
        assertThat(result.getMainPlace()).isEqualTo(selectStore.getMainPlace());
        assertThat(result.getDetailPlace()).isEqualTo(selectStore.getDetailPlace());
        assertThat(result.getBankAccountNumber()).isEqualTo(selectStore.getBankAccountNumber());
        assertThat(result.getDefaultEarningRate()).isEqualTo(selectStore.getDefaultEarningRate());
        assertThat(result.getPathName()).isEqualTo(selectStore.getPathName());

        verify(storeRepository, times(1)).findById(store.getId());
        verify(storeRepository, times(1)).lookupStore(account.getId(), store.getId());
        verify(objectStorageService, times(1)).getFullPath(anyString(), anyString());
    }

    @Test
    @DisplayName("사용자 : 매장 정보 조회 - 성공")
    void selectStoreForUser() {
        Store store = tpe.getOpenStoreByOneAccount(account);
        ReflectionTestUtils.setField(store, "id", 1L);
        store.modifyAddress(new Address("광주 지산동", "조선대 정문", BigDecimal.ONE, BigDecimal.ONE));

        SelectStoreForUserResponseDto selectStore = new SelectStoreForUserResponseDto(
            store.getBusinessLicenseNumber(),
            store.getRepresentativeName(),
            store.getOpeningDate(),
            store.getName(),
            store.getPhoneNumber(),
            store.getAddress().getMainPlace(),
            store.getAddress().getDetailPlace(),
            store.getDescription(),
            store.getStoreImage().getSavedName()
        );

        when(storeRepository.lookupStoreForUser(store.getId())).thenReturn(Optional.of(selectStore));
        selectStore.setSavedName(objectStorageService.getFullPath(FileDomain.STORE_IMAGE.getVariable(), selectStore.getSavedName()));

        SelectStoreForUserResponseDto result = storeService.selectStoreForUser(store.getId());

        assertThat(result.getStoreName()).isEqualTo(selectStore.getStoreName());
        assertThat(result.getRepresentativeName()).isEqualTo(selectStore.getRepresentativeName());
        assertThat(result.getBusinessLicenseNumber()).isEqualTo(selectStore.getBusinessLicenseNumber());
        assertThat(result.getOpeningDate()).isEqualTo(selectStore.getOpeningDate());
        assertThat(result.getMainPlace()).isEqualTo(selectStore.getMainPlace());
        assertThat(result.getDetailPlace()).isEqualTo(selectStore.getDetailPlace());
        assertThat(result.getSavedName()).isEqualTo(selectStore.getSavedName());
        assertThat(result.getDescription()).isEqualTo(selectStore.getDescription());

        verify(storeRepository, times(1)).lookupStoreForUser(store.getId());
        verify(objectStorageService, times(1)).getFullPath(anyString(), anyString());
    }

    @Test
    @DisplayName("매장 등록 - 성공")
    void createStore() throws IOException {
        Merchant merchant = te.getMerchant();
        ReflectionTestUtils.setField(merchant, "id", 1L);
        BankType bankType = te.getBankTypeKb();
        StoreStatus storeStatus = te.getStoreStatusClose();
        StoreCategory storeCategory = te.getStoreCategory();
        CreateStoreRequestDto createStoreRequestDto = te.getCreateStoreRequestDto(merchant, bankType);
        Store store = tpe.getOpenStoreByOneAccount(account);
        ReflectionTestUtils.setField(store, "id", 1L);
        MultipartFile businessImage = new MockMultipartFile(UUID.randomUUID() + ".jpg",
            "business_image.jpg",
            "image/png",
            new byte[0]);

        MultipartFile storeImage = new MockMultipartFile(UUID.randomUUID() + ".jpg",
            "store_image.jpg",
            "image/png",
            new byte[0]);

        when(storeRepository.existsStoreByBusinessLicenseNumber(createStoreRequestDto.getBusinessLicenseNumber())).thenReturn(false);

        if (Objects.nonNull(createStoreRequestDto.getMerchantId())) {
            when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        }
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(bankTypeRepository.findById(createStoreRequestDto.getBankCode())).thenReturn(Optional.of(bankType));
        when(storeStatusRepository.getReferenceById(StoreStatus.StoreStatusCode.CLOSE.name())).thenReturn(storeStatus);
        when(storeCategoryRepository.findById(createStoreRequestDto.getStoreCategories().get(0))).thenReturn(Optional.of(storeCategory));
        when(storeRepository.save(any(Store.class))).thenReturn(store);


        Long storeId = storeService.createStore(account.getId(), createStoreRequestDto, businessImage, storeImage);

        verify(storeRepository, times(1)).existsStoreByBusinessLicenseNumber(createStoreRequestDto.getBusinessLicenseNumber());
        verify(merchantRepository, times(1)).findById(createStoreRequestDto.getMerchantId());
        verify(accountRepository, times(1)).findById(account.getId());
        verify(bankTypeRepository, times(1)).findById(createStoreRequestDto.getBankCode());
        verify(storeStatusRepository, times(1)).getReferenceById(StoreStatus.StoreStatusCode.CLOSE.name());
        verify(objectStorageService, times(2)).storeFile(any(), anyString(), anyBoolean());
        verify(storeCategoryRepository, times(1)).findById(anyString());
        verify(storeRepository, times(1)).save(any(Store.class));

        assertThat(storeId).isEqualTo(store.getId());
    }

    @Test
    @DisplayName("매장 등록 - 실패")
    void createStore_fail() throws IOException {
        Merchant merchant = te.getMerchant();
        ReflectionTestUtils.setField(merchant, "id", 1L);
        BankType bankType = te.getBankTypeKb();
        StoreStatus storeStatus = te.getStoreStatusClose();
        StoreCategory storeCategory = te.getStoreCategory();
        CreateStoreRequestDto createStoreRequestDto = te.getCreateStoreRequestDto(merchant, bankType);
        MultipartFile businessImage = new MockMultipartFile(UUID.randomUUID() + ".jpg",
            "business_image.jpg",
            "image/png",
            new byte[0]);

        MultipartFile storeImage = new MockMultipartFile(UUID.randomUUID() + ".jpg",
            "store_image.jpg",
            "image/png",
            new byte[0]);

        when(storeRepository.existsStoreByBusinessLicenseNumber(createStoreRequestDto.getBusinessLicenseNumber())).thenReturn(true);

        assertThatThrownBy(
            () -> storeService.createStore(account.getId(), createStoreRequestDto, businessImage, storeImage))
            .isInstanceOf(DuplicatedBusinessLicenseException.class)
            .hasMessageContaining(createStoreRequestDto.getBusinessLicenseNumber() + "은 이미 등록되어 있습니다.");

        verify(storeRepository, times(1)).existsStoreByBusinessLicenseNumber(createStoreRequestDto.getBusinessLicenseNumber());
        verify(merchantRepository, times(0)).findById(createStoreRequestDto.getMerchantId());
        verify(accountRepository, times(0)).findById(account.getId());
        verify(bankTypeRepository, times(0)).findById(createStoreRequestDto.getBankCode());
        verify(storeStatusRepository, times(0)).getReferenceById(StoreStatus.StoreStatusCode.CLOSE.name());
        verify(objectStorageService, times(0)).storeFile(any(), anyString(), anyBoolean());
        verify(storeCategoryRepository, times(0)).findById(anyString());
        verify(storeRepository, times(0)).save(any(Store.class));

    }

    @Test
    @DisplayName("매장 정보 수정 - 성공")
    void updateStore() throws IOException {
        Store store = tpe.getOpenStoreByOneAccount(account);
        Address address = te.getAddress();
        store.modifyAddress(address);
        BankType bankType = te.getBankTypeKb();
        ReflectionTestUtils.setField(store, "id", 1L);
        UpdateStoreRequestDto updateStoreRequestDto = ReflectionUtils.newInstance(UpdateStoreRequestDto.class);
        ReflectionTestUtils.setField(updateStoreRequestDto, "businessLicenseNumber", store.getBusinessLicenseNumber());
        ReflectionTestUtils.setField(updateStoreRequestDto, "representativeName", store.getRepresentativeName());
        ReflectionTestUtils.setField(updateStoreRequestDto, "openingDate", store.getOpeningDate());
        ReflectionTestUtils.setField(updateStoreRequestDto, "storeName", store.getName());
        ReflectionTestUtils.setField(updateStoreRequestDto, "mainPlace", address.getMainPlace());
        ReflectionTestUtils.setField(updateStoreRequestDto, "detailPlace", address.getDetailPlace());
        ReflectionTestUtils.setField(updateStoreRequestDto, "latitude", address.getLatitude());
        ReflectionTestUtils.setField(updateStoreRequestDto, "longitude", address.getLongitude());
        ReflectionTestUtils.setField(updateStoreRequestDto, "phoneNumber", store.getPhoneNumber());
        ReflectionTestUtils.setField(updateStoreRequestDto, "earningRate", store.getDefaultEarningRate());
        ReflectionTestUtils.setField(updateStoreRequestDto, "bankCode", bankType.getBankTypeCode());
        ReflectionTestUtils.setField(updateStoreRequestDto, "bankAccount", store.getBankAccountNumber());
        ReflectionTestUtils.setField(updateStoreRequestDto, "savedName", store.getStoreImage().getSavedName());
        MultipartFile storeImage = new MockMultipartFile(UUID.randomUUID() + ".jpg",
            "store_image.jpg",
            "image/png",
            new byte[0]);

        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(bankTypeRepository.findById(updateStoreRequestDto.getBankCode())).thenReturn(Optional.of(bankType));

        storeService.updateStore(account.getId(), store.getId(), updateStoreRequestDto, storeImage);

        verify(storeRepository, times(1)).findById(store.getId());
        verify(accountRepository, times(1)).findById(account.getId());
        verify(bankTypeRepository, times(1)).findById(updateStoreRequestDto.getBankCode());
        verify(objectStorageService, times(1)).storeFile(storeImage, FileDomain.STORE_IMAGE.getVariable(), true);

    }

    @Test
    void updateStoreCategories() {
    }

    @Test
    void updateStoreStatus() {
    }
}
