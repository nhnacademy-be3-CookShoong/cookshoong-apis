package store.cookshoong.www.cookshoongbackend.shop.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.el.util.ReflectionUtil;
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
import store.cookshoong.www.cookshoongbackend.address.model.response.AddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.service.AddressService;
import store.cookshoong.www.cookshoongbackend.common.property.ObjectStorageProperties;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.model.LocationType;
import store.cookshoong.www.cookshoongbackend.file.repository.ImageRepository;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtilResolver;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtils;
import store.cookshoong.www.cookshoongbackend.file.service.LocalFileService;
import store.cookshoong.www.cookshoongbackend.file.service.ObjectStorageAuth;
import store.cookshoong.www.cookshoongbackend.file.service.ObjectStorageService;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreCategory;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.DuplicatedBusinessLicenseException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreInfoRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreManagerRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreStatusRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreCategoriesDto;
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
    private StoreStatusRepository storeStatusRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private StoreCategoryRepository storeCategoryRepository;
    @Mock
    private FileUtilResolver fileUtilResolver;
    @Mock
    private List<FileUtils> fileUtilsList;
    @Mock
    private ObjectStorageAuth objectStorageAuth;
    @Mock
    private ObjectStorageProperties objectStorageProperties;
    @Mock
    private FileUtils fileUtils;
    @Mock
    private ObjectStorageService objectStorageService;
    @Mock
    private LocalFileService localFileService;
    @Mock
    private AddressService addressService;
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
    @DisplayName("사장님 매장조회 : 아무 매장이 없는 경우 - empty로 반환")
    void selectAllStore_fail(){
        List<SelectAllStoresResponseDto> emptyStoreList = Collections.emptyList();

        when(storeRepository.lookupStores(account.getId())).thenReturn(emptyStoreList);

        List<SelectAllStoresResponseDto> result = storeService.selectAllStores(account.getId());

        verify(storeRepository, times(1)).lookupStores(account.getId());

        assertThat(result).isEqualTo(emptyStoreList);
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
            store.getMinimumOrderPrice(),
            store.getDescription(),
            store.getBankTypeCode().getBankTypeCode(),
            store.getBankAccountNumber(),
            store.getStoreImage().getSavedName(),
            store.getStoreImage().getLocationType(),
            store.getStoreImage().getDomainName(),
            store.getDeliveryCost(),
            store.getStoreStatus().getDescription());
        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        when(fileUtilResolver.getFileService(store.getStoreImage().getLocationType())).thenReturn(objectStorageService);
        when(storeRepository.lookupStore(account.getId(), store.getId())).thenReturn(Optional.of(selectStore));
        when(fileUtilResolver.getFileService(store.getStoreImage().getLocationType()))
            .thenReturn(objectStorageService);

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
    }

    @Test
    @DisplayName("사용자 : 매장 정보 조회 - 성공")
    void selectStoreForUser() {
        Store store = tpe.getOpenStoreByOneAccount(account);
        Address address = new Address("광주 지산동", "조선대 정문", BigDecimal.ONE, BigDecimal.ONE);
        ReflectionTestUtils.setField(store, "id", 1L);
        store.modifyAddress(address);

        SelectStoreForUserResponseDto selectStore = new SelectStoreForUserResponseDto(
            store.getBusinessLicenseNumber(),
            store.getRepresentativeName(),
            store.getOpeningDate(),
            store.getName(),
            store.getPhoneNumber(),
            store.getAddress().getMainPlace(),
            store.getAddress().getDetailPlace(),
            store.getDescription(),
            store.getStoreImage().getLocationType(),
            store.getStoreImage().getDomainName(),
            store.getStoreImage().getSavedName(),
            store.getMinimumOrderPrice(),
            store.getDeliveryCost(),
            store.getStoreStatus().getDescription()
        );

        AddressResponseDto addressResponseDto = new AddressResponseDto(
            address.getId(),
            address.getMainPlace(),
            address.getDetailPlace(),
            address.getLatitude(),
            address.getLongitude()
        );

        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        when(storeRepository.lookupStoreForUser(store.getId())).thenReturn(Optional.of(selectStore));
        when(fileUtilResolver.getFileService(store.getStoreImage().getLocationType()))
            .thenReturn(objectStorageService);
        when(addressService.selectAccountChoiceAddress(address.getId())).thenReturn(addressResponseDto);
        selectStore.setSavedName(objectStorageService
            .getFullPath(store.getStoreImage().getDomainName(), selectStore.getSavedName()));

        SelectStoreForUserResponseDto result = storeService.selectStoreForUser(address.getId(), store.getId());

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
        Map<String, MultipartFile> fileMap = new HashMap<>();
        fileMap.put(FileDomain.BUSINESS_INFO_IMAGE.getVariable(), businessImage);
        fileMap.put(FileDomain.STORE_IMAGE.getVariable(), storeImage);

        when(storeRepository.existsStoreByBusinessLicenseNumber(createStoreRequestDto.getBusinessLicenseNumber())).thenReturn(false);

        if (Objects.nonNull(createStoreRequestDto.getMerchantId())) {
            when(merchantRepository.findById(merchant.getId())).thenReturn(Optional.of(merchant));
        }
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(bankTypeRepository.findById(createStoreRequestDto.getBankCode())).thenReturn(Optional.of(bankType));
        when(storeStatusRepository.getReferenceById(StoreStatus.StoreStatusCode.CLOSE.name())).thenReturn(storeStatus);
        when(fileUtilResolver.getFileService(LocationType.OBJECT_S.getVariable())).thenReturn(objectStorageService);

        Image image = mock(Image.class);
        when(imageRepository.findById(anyLong())).thenReturn(Optional.ofNullable(image));

        when(storeCategoryRepository.findById(createStoreRequestDto.getStoreCategories().get(0))).thenReturn(Optional.of(storeCategory));
        when(storeRepository.save(any(Store.class))).thenReturn(store);


        Long storeId = storeService.createStore(account.getId(), createStoreRequestDto, LocationType.OBJECT_S.getVariable(), fileMap);

        verify(storeRepository, times(1)).existsStoreByBusinessLicenseNumber(createStoreRequestDto.getBusinessLicenseNumber());
        verify(merchantRepository, times(1)).findById(createStoreRequestDto.getMerchantId());
        verify(accountRepository, times(1)).findById(account.getId());
        verify(bankTypeRepository, times(1)).findById(createStoreRequestDto.getBankCode());
        verify(storeStatusRepository, times(1)).getReferenceById(StoreStatus.StoreStatusCode.CLOSE.name());
        verify(fileUtilResolver, times(1)).getFileService(anyString());
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
        Map<String, MultipartFile> fileMap = new HashMap<>();
        fileMap.put(FileDomain.BUSINESS_INFO_IMAGE.getVariable(), businessImage);
        fileMap.put(FileDomain.STORE_IMAGE.getVariable(), storeImage);

        when(storeRepository.existsStoreByBusinessLicenseNumber(createStoreRequestDto.getBusinessLicenseNumber())).thenReturn(true);

        assertThatThrownBy(
            () -> storeService.createStore(account.getId(), createStoreRequestDto, LocationType.OBJECT_S.getVariable(),fileMap))
            .isInstanceOf(DuplicatedBusinessLicenseException.class)
            .hasMessageContaining(createStoreRequestDto.getBusinessLicenseNumber() + "은 이미 등록되어 있습니다.");

        verify(storeRepository, times(1)).existsStoreByBusinessLicenseNumber(createStoreRequestDto.getBusinessLicenseNumber());
        verify(merchantRepository, times(0)).findById(createStoreRequestDto.getMerchantId());
        verify(accountRepository, times(0)).findById(account.getId());
        verify(bankTypeRepository, times(0)).findById(createStoreRequestDto.getBankCode());
        verify(storeStatusRepository, times(0)).getReferenceById(StoreStatus.StoreStatusCode.CLOSE.name());
        //verify(objectStorageService, times(0)).storeFile(any(), anyString(), anyBoolean());
        verify(storeCategoryRepository, times(0)).findById(anyString());
        verify(storeRepository, times(0)).save(any(Store.class));

    }

    @Test
    @DisplayName("매장 정보 수정 - 성공")
    void updateStore(){
        Store store = tpe.getOpenStoreByOneAccount(account);
        BankType bankType = te.getBankTypeKb();
        ReflectionTestUtils.setField(store, "id", 1L);
        UpdateStoreManagerRequestDto updateStoreManagerRequestDto = ReflectionUtils.newInstance(UpdateStoreManagerRequestDto.class);
        ReflectionTestUtils.setField(updateStoreManagerRequestDto, "representativeName", store.getRepresentativeName());
        ReflectionTestUtils.setField(updateStoreManagerRequestDto, "bankCode", bankType.getBankTypeCode());
        ReflectionTestUtils.setField(updateStoreManagerRequestDto, "bankAccount", store.getBankAccountNumber());

        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        when(bankTypeRepository.findById(updateStoreManagerRequestDto.getBankCode())).thenReturn(Optional.of(bankType));

        storeService.updateStore(account.getId(), store.getId(), updateStoreManagerRequestDto);

        verify(storeRepository, times(1)).findById(store.getId());
        verify(bankTypeRepository, times(1)).findById(updateStoreManagerRequestDto.getBankCode());


    }

    @Test
    @DisplayName("매장정보 수정 - 성공")
    void updateStoreCategories() {
        Store store = tpe.getOpenStoreByOneAccount(account);
        Address address = te.getAddress();
        store.modifyAddress(address);

        ReflectionTestUtils.setField(store, "id", 1L);
        UpdateStoreInfoRequestDto storeInfoRequestDto = ReflectionUtils.newInstance(UpdateStoreInfoRequestDto.class);
        ReflectionTestUtils.setField(storeInfoRequestDto, "openingDate", LocalDate.parse("2022-02-10"));
        ReflectionTestUtils.setField(storeInfoRequestDto, "storeName", "엄마네 돼지찌개");
        ReflectionTestUtils.setField(storeInfoRequestDto, "mainPlace", "광주광역시 동구 용산동");
        ReflectionTestUtils.setField(storeInfoRequestDto, "detailPlace", "200-1길");
        ReflectionTestUtils.setField(storeInfoRequestDto, "latitude", new BigDecimal("32.000000000"));
        ReflectionTestUtils.setField(storeInfoRequestDto,"longitude", new BigDecimal("32.000000000"));
        ReflectionTestUtils.setField(storeInfoRequestDto, "phoneNumber", "01099890201");
        ReflectionTestUtils.setField(storeInfoRequestDto, "description", "우리가게 맛집입니다~~");
        ReflectionTestUtils.setField(storeInfoRequestDto, "earningRate", new BigDecimal("2.2"));
        ReflectionTestUtils.setField(storeInfoRequestDto, "minimumOrderPrice", 0);
        ReflectionTestUtils.setField(storeInfoRequestDto, "deliveryCost", 4000);

        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        assertThat(store.getAccount().getId()).isEqualTo(account.getId());

        storeService.updateStoreInfo(account.getId(), store.getId(), storeInfoRequestDto);

        verify(storeRepository, times(1)).findById(store.getId());

    }

    @Test
    @DisplayName("매장 상태 변경 - 성공")
    void updateStoreStatus() {
        Store store = tpe.getOpenStoreByOneAccount(account);

        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        assertThat(store.getAccount().getId()).isEqualTo(account.getId());
        assertThat(store.getStoreStatus().getDescription()).isNotEqualTo("폐업");
        when(storeStatusRepository.findById("CLOSE")).thenReturn(Optional.of(new StoreStatus("CLOSE", "휴식중")));

        storeService.updateStoreStatus(account.getId(), store.getId(), "CLOSE");

        verify(storeRepository, times(1)).findById(store.getId());
        verify(storeStatusRepository, times(1)).findById(store.getStoreStatus().getCode());
    }

    @Test
    @DisplayName("매장 상태 변경 - 실패 : 존재하지 않는 매장 상태 코드 입력")
    void updateStoreStatus_fail() {
        Store store = tpe.getOpenStoreByOneAccount(account);

        when(storeRepository.findById(store.getId())).thenReturn(Optional.of(store));
        assertThat(store.getAccount().getId()).isEqualTo(account.getId());
        assertThat(store.getStoreStatus().getDescription()).isNotEqualTo("폐업");
        when(storeStatusRepository.findById("NONE")).thenThrow(StoreStatusNotFoundException.class);

       assertThatThrownBy(
            () -> storeService.updateStoreStatus(account.getId(), store.getId(), "NONE"))
           .isInstanceOf(StoreStatusNotFoundException.class);
        verify(storeRepository, times(1)).findById(store.getId());
    }
}
