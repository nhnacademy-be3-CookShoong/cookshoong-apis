package store.cookshoong.www.cookshoongbackend.shop.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.address.model.response.AddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.repository.accountaddress.AccountAddressRepository;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.repository.ImageRepository;
import store.cookshoong.www.cookshoongbackend.file.service.ObjectStorageService;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreCategory;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoresHasCategory;
import store.cookshoong.www.cookshoongbackend.shop.exception.banktype.BankTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.category.StoreCategoryNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.merchant.MerchantNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.DuplicatedBusinessLicenseException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.StoreStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.UserAccessDeniedException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreStatusRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresNotOutedResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreResponseTemp;
import store.cookshoong.www.cookshoongbackend.shop.repository.bank.BankTypeRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.category.StoreCategoryRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.merchant.MerchantRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.stauts.StoreStatusRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;

/**
 * 매장리스트 조회, 등록, 삭제, 수정 서비스 구현.
 *
 * @author seungyeon (유승연)
 * @contributer jeongjewan (정제완)
 * @since 2023.07.05
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final StoreRepository storeRepository;
    private final BankTypeRepository bankTypeRepository;
    private final AccountRepository accountRepository;
    private final MerchantRepository merchantRepository;
    private final StoreStatusRepository storeStatusRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final AccountAddressRepository accountAddressRepository;
    private final ImageRepository imageRepository;
    private static final BigDecimal DISTANCE = new BigDecimal("3.0");
    private static final Double RADIUS = 6371.0;
    private static final Double TO_RADIAN = Math.PI / 180;
    private final ObjectStorageService objectStorageService;

    private static void accessDeniedException(Long accountId, Store store) {
        if (!store.getAccount().getId().equals(accountId)) {
            throw new UserAccessDeniedException("해당 매장에 대한 접근 권한이 없습니다.");
        }
    }

    private void addStoreCategory(List<String> categories, Store store) {
        if (categories.size() < 4) {
            for (String categoryCode : categories) {
                StoreCategory category = storeCategoryRepository.findById(categoryCode)
                    .orElseThrow(StoreCategoryNotFoundException::new);
                store.getStoresHasCategories()
                    .add(new StoresHasCategory(new StoresHasCategory.Pk(store.getId(), category.getCategoryCode()), store, category));
            }
        }
    }

    /**
     * 사업자 회원 : 매장을 pagination 으로 작성.
     *
     * @param accountId 회원아이디
     * @param pageable  페이지 정보
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<SelectAllStoresResponseDto> selectAllStores(Long accountId, Pageable pageable) {
        return storeRepository.lookupStoresPage(accountId, pageable);
    }

    /**
     * 사업자 회원 : 매장 조회.
     *
     * @param accountId 회원 id
     * @param storeId   매장 id
     * @return 매장 정보
     */
    @Transactional(readOnly = true)
    public SelectStoreResponseDto selectStore(Long accountId, Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);
        accessDeniedException(accountId, store);
        SelectStoreResponseTemp responseVo = storeRepository.lookupStore(accountId, storeId)
            .orElseThrow(StoreNotFoundException::new);
        String pathName = objectStorageService
            .getFullPath(FileDomain.STORE_IMAGE.getVariable(), responseVo.getSavedName());
        return new SelectStoreResponseDto(responseVo, pathName);
    }

    /**
     * 일반 유저 : 매장 정보 조회.
     *
     * @param storeId 매장 아이디
     * @return 매장 정보 조회
     */
    public SelectStoreForUserResponseDto selectStoreForUser(Long storeId) {
        SelectStoreForUserResponseDto responseDto = storeRepository.lookupStoreForUser(storeId)
            .orElseThrow(StoreNotFoundException::new);
        responseDto.setSavedName(objectStorageService.getFullPath(FileDomain.STORE_IMAGE.getVariable(), responseDto.getSavedName()));
        return responseDto;
    }

    /**
     * 사업자 : 매장 등록 서비스 구현.
     * 가맹점 등록시 찾아서 넣고, 없으면 null로 등록, 매장 등록시 바로 CLOSE(휴식중) 상태로 등록됨.
     *
     * @param accountId          회원 아이디
     * @param registerRequestDto 매장 등록을 위한 정보
     * @param businessImage      the business image
     * @param storeImage         the store image
     * @throws IOException the io exception
     */
    public void createStore(Long accountId, CreateStoreRequestDto registerRequestDto,
                            MultipartFile businessImage, MultipartFile storeImage) throws IOException {
        if (storeRepository.existsStoreByBusinessLicenseNumber(registerRequestDto.getBusinessLicenseNumber())) {
            throw new DuplicatedBusinessLicenseException(registerRequestDto.getBusinessLicenseNumber());
        }
        Merchant merchant = null;
        if(Objects.nonNull(registerRequestDto.getMerchantId())){
            merchant = merchantRepository.findById(registerRequestDto.getMerchantId())
                .orElseThrow(MerchantNotFoundException::new);
        }
        Account account = accountRepository.findById(accountId)
            .orElseThrow(UserNotFoundException::new);
        BankType bankType = bankTypeRepository.findById(registerRequestDto.getBankCode())
            .orElseThrow(BankTypeNotFoundException::new);
        StoreStatus storeStatus = storeStatusRepository.getReferenceById(StoreStatus.StoreStatusCode.CLOSE.name());
        Image businessLicenseImage = objectStorageService
            .storeFile(businessImage, FileDomain.BUSINESS_INFO_IMAGE.getVariable(), false);
        Image storeMainImage = objectStorageService.storeFile(storeImage, FileDomain.STORE_IMAGE.getVariable(), true);
        Store store = new Store(merchant,
            account,
            bankType,
            storeStatus,
            businessLicenseImage,
            registerRequestDto.getBusinessLicenseNumber(),
            registerRequestDto.getRepresentativeName(),
            registerRequestDto.getOpeningDate(),
            registerRequestDto.getStoreName(),
            registerRequestDto.getPhoneNumber(),
            registerRequestDto.getEarningRate(),
            registerRequestDto.getDescription(),
            storeMainImage,
            registerRequestDto.getBankAccount());

        List<String> categories = registerRequestDto.getStoreCategories();
        addStoreCategory(categories, store);

        Address address = new Address(registerRequestDto.getMainPlace(), registerRequestDto.getDetailPlace(),
            registerRequestDto.getLatitude(), registerRequestDto.getLongitude());
        store.modifyAddress(address);

        storeRepository.save(store);
    }

    /**
     * 사업자 : 매장 정보 수정.
     *
     * @param accountId  the account id
     * @param storeId    the store id
     * @param requestDto 매장 수정 정보
     * @param storeImage the store image
     * @throws IOException the io exception
     */
    public void updateStore(Long accountId, Long storeId, UpdateStoreRequestDto requestDto, MultipartFile storeImage) throws IOException {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        accessDeniedException(accountId, store);
        Account account = accountRepository.findById(accountId)
            .orElseThrow(UserNotFoundException::new);
        BankType bankType = bankTypeRepository.findByDescription(requestDto.getBankName())
            .orElseThrow(BankTypeNotFoundException::new);
        StoreStatus storeStatus = store.getStoreStatusCode();

        Image storeMainImage = objectStorageService.storeFile(storeImage, FileDomain.STORE_IMAGE.getVariable(), true);
        store.modifyStoreInfo(
            account,
            bankType,
            storeStatus,
            requestDto.getBusinessLicenseNumber(),
            requestDto.getRepresentativeName(),
            requestDto.getOpeningDate(),
            requestDto.getStoreName(),
            requestDto.getPhoneNumber(),
            requestDto.getEarningRate(),
            requestDto.getDescription(),
            storeMainImage,
            requestDto.getBankAccount()
        );

        Address address = new Address(requestDto.getMainPlace(), requestDto.getDetailPlace(),
            requestDto.getLatitude(), requestDto.getLongitude());
        store.modifyAddress(address);
    }

    /**
     * 사업자 : 매장 카테고리 수정.
     *
     * @param accountId  the account id
     * @param storeId    the store id
     * @param requestDto 매장 카테고리 code list
     */
    public void updateStoreCategories(Long accountId, Long storeId, UpdateCategoryRequestDto requestDto) {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        accessDeniedException(accountId, store);

        store.initStoreCategories();

        addStoreCategory(requestDto.getStoreCategories(), store);
    }

    /**
     * 사업자 : 매장 상태 변경.
     *
     * @param accountId  the account id
     * @param storeId    the store id
     * @param requestDto 매장 상태 변경 코드
     */
    public void updateStoreStatus(Long accountId, Long storeId, UpdateStoreStatusRequestDto requestDto) {
        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);
        accessDeniedException(accountId, store);
        StoreStatus storeStatus = storeStatusRepository.findById(requestDto.getStatusCode())
            .orElseThrow(StoreStatusNotFoundException::new);
        store.modifyStoreStatus(storeStatus);
    }

    /**
     * 회원의 위치를 기반으로 3km 이내에 위차한 매장만을 조회하는 메서드.
     *
     * @param addressId 주소 아이디
     * @param pageable  페이지 정보
     * @return 3km 이내에 위치한 매장만을 반환
     * @author jeongjewan
     */
    public Page<SelectAllStoresNotOutedResponseDto> selectAllStoresNotOutedResponsePage(Long addressId,
                                                                                        Pageable pageable) {
        Page<SelectAllStoresNotOutedResponseDto> allStore =
            storeRepository.lookupStoreLatLanPage(pageable);
        AddressResponseDto addressLatLng =
            accountAddressRepository.lookupByAccountSelectAddressId(addressId);

        List<SelectAllStoresNotOutedResponseDto> nearbyStores = allStore
            .stream()
            .filter(store -> isWithDistance(addressLatLng, store))
            .collect(Collectors.toList());
        nearbyStores.forEach(selectAllStoresNotOutedResponseDto ->
            selectAllStoresNotOutedResponseDto.setSavedName(
                objectStorageService.getFullPath(FileDomain.STORE_IMAGE.getVariable(), selectAllStoresNotOutedResponseDto.getSavedName())));
        return new PageImpl<>(nearbyStores, pageable, nearbyStores.size());
    }

    private boolean isWithDistance(AddressResponseDto address, SelectAllStoresNotOutedResponseDto store) {

        BigDecimal storeDistance = calculateDistance(
            address.getLatitude(), address.getLongitude(),
            store.getLatitude(), store.getLongitude()
        );

        return storeDistance.compareTo(DISTANCE) <= 0;
    }

    private BigDecimal calculateDistance(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {

        Double distance;

        Double deltaLatitude = Math.abs(x1.doubleValue() - x2.doubleValue()) * TO_RADIAN;
        Double deltaLongitude = Math.abs(y1.doubleValue() - y2.doubleValue()) * TO_RADIAN;

        Double sinDeltaLat = Math.sin(deltaLatitude / 2);
        Double sinDeltaLng = Math.sin(deltaLongitude / 2);

        Double mulSinDelLat = sinDeltaLat * sinDeltaLat;
        Double cosX1ToTadian = Math.cos(x1.doubleValue() * TO_RADIAN);
        Double cosX2ToTadian = Math.cos(x2.doubleValue() * TO_RADIAN);
        Double mulSinDelLng = sinDeltaLng * sinDeltaLng;

        Double squareRoot = Math.sqrt(
            mulSinDelLat + cosX1ToTadian * cosX2ToTadian * mulSinDelLng);

        distance = 2 * RADIUS * Math.asin(squareRoot);

        return BigDecimal.valueOf(distance);
    }
}
