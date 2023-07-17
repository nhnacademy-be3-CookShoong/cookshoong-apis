package store.cookshoong.www.cookshoongbackend.shop.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.address.model.response.AddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.repository.accountaddress.AccountAddressRepository;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreCategory;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoresHasCategory;
import store.cookshoong.www.cookshoongbackend.shop.exception.banktype.BankTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.category.StoreCategoryNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.DuplicatedBusinessLicenseException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.SelectStoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.UserAccessDeniedException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateCategoryRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresNotOutedResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.bank.BankTypeRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.category.StoreCategoryRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.merchant.MerchantRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.store.StoreStatusRepository;

/**
 * 매장리스트 조회, 등록, 삭제, 수정 서비스 구현.
 *
 * @author seungyeon
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
    private static final BigDecimal DISTANCE = new BigDecimal("3.0");

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
        return storeRepository.lookupStore(accountId, storeId)
            .orElseThrow(SelectStoreNotFoundException::new);
    }

    /**
     * 일반 유저 : 매장 정보 조회.
     *
     * @param storeId 매장 아이디
     * @return 매장 정보 조회
     */
    @Transactional(readOnly = true)
    public SelectStoreForUserResponseDto selectStoreForUser(Long storeId) {
        return storeRepository.lookupStoreForUser(storeId)
            .orElseThrow(SelectStoreNotFoundException::new);
    }

    //TODO 2. 매장에서 카테고리 설정하는거 빠져있음. 추후에 프론트 적용하면서 넘어오는 값들 확인 후 다시 설정.

    /**
     * 사업자 : 매장 등록 서비스 구현.
     * 가맹점 등록시 찾아서 넣고, 없으면 null로 등록, 매장 등록시 바로 CLOSE(휴식중) 상태로 등록됨.
     *
     * @param accountId          회원 아이디
     * @param registerRequestDto 매장 등록을 위한 정보
     */
    public void createStore(Long accountId, CreateStoreRequestDto registerRequestDto) {
        if (storeRepository.existsStoreByBusinessLicenseNumber(registerRequestDto.getBusinessLicenseNumber())) {
            throw new DuplicatedBusinessLicenseException(registerRequestDto.getBusinessLicenseNumber());
        }

        Merchant merchant = merchantRepository.findById(registerRequestDto.getMerchantId()).orElse(null);
        Account account = accountRepository.findById(accountId)
            .orElseThrow(UserNotFoundException::new);
        BankType bankType = bankTypeRepository.findById(registerRequestDto.getBankCode())
            .orElseThrow(BankTypeNotFoundException::new);
        StoreStatus storeStatus = storeStatusRepository.getReferenceById(StoreStatus.StoreStatusCode.CLOSE.name());

        Store store = new Store(merchant,
            account,
            bankType,
            storeStatus,
            registerRequestDto.getBusinessLicense(),
            registerRequestDto.getBusinessLicenseNumber(),
            registerRequestDto.getRepresentativeName(),
            registerRequestDto.getOpeningDate(),
            registerRequestDto.getStoreName(),
            registerRequestDto.getPhoneNumber(),
            registerRequestDto.getEarningRate(),
            registerRequestDto.getDescription(),
            null,
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
     */
    public void updateStore(Long accountId, Long storeId, UpdateStoreRequestDto requestDto) {
        Store store = storeRepository.findById(storeId).orElseThrow(SelectStoreNotFoundException::new);
        accessDeniedException(accountId, store);
        Merchant merchant = merchantRepository.findMerchantByName(requestDto.getMerchantName()).orElse(null);
        Account account = accountRepository.findById(accountId)
            .orElseThrow(UserNotFoundException::new);
        BankType bankType = bankTypeRepository.findByDescription(requestDto.getBankName())
            .orElseThrow(BankTypeNotFoundException::new);
        StoreStatus storeStatus = store.getStoreStatusCode();
        store.modifyStoreInfo(
            merchant,
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
            requestDto.getImage(),
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
        Store store = storeRepository.findById(storeId).orElseThrow(SelectStoreNotFoundException::new);
        accessDeniedException(accountId, store);

        store.initStoreCategories();

        addStoreCategory(requestDto.getStoreCategories(), store);
    }

    /**
     * 회원의 위치를 기반으로 3km 이내에 위차한 매장만을 조회하는 메서드.
     *
     * @param addressId 주소 아이디
     * @param pageable  페이지 정보
     * @return          3km 이내에 위치한 매장만을 반환
     */
    public Page<SelectAllStoresNotOutedResponseDto> selectAllStoresNotOutedResponsePage(Long addressId,
                                                                                        Pageable pageable) {
        Page<SelectAllStoresNotOutedResponseDto> allStore =
            storeRepository.lookupStoreLatLanPage(pageable);
        AddressResponseDto addressLatLng =
            accountAddressRepository.lookupByAccountSelectAddressId(addressId);
        log.info("ADDRESS: {}", addressLatLng);
        List<SelectAllStoresNotOutedResponseDto> nearbyStores = allStore
            .stream()
            .filter(store -> isWithDistance(addressLatLng, store))
            .collect(Collectors.toList());
        return new PageImpl<>(nearbyStores, pageable, nearbyStores.size());
    }

    private boolean isWithDistance(AddressResponseDto address, SelectAllStoresNotOutedResponseDto store) {

        BigDecimal storeDistance =  calculateDistance(
            address.getLatitude(), address.getLongitude(),
            store.getLatitude(), store.getLongitude()
        );

        return storeDistance.compareTo(DISTANCE) <= 0;
    }

    private BigDecimal calculateDistance(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {

        Double distance;
        Double radius = 6371.0; // 지구 반지름(km)
        Double toRadian = Math.PI / 180;

        Double deltaLatitude = Math.abs(x1.doubleValue() - x2.doubleValue()) * toRadian;
        Double deltaLongitude = Math.abs(y1.doubleValue() - y2.doubleValue()) * toRadian;

        Double sinDeltaLat = Math.sin(deltaLatitude / 2);
        Double sinDeltaLng = Math.sin(deltaLongitude / 2);

        Double mulSinDelLat = sinDeltaLat * sinDeltaLat;
        Double cosX1ToTadian = Math.cos(x1.doubleValue() * toRadian);
        Double cosX2ToTadian = Math.cos(x2.doubleValue() * toRadian);
        Double mulSinDelLng = sinDeltaLng * sinDeltaLng;

        Double squareRoot = Math.sqrt(
            mulSinDelLat + cosX1ToTadian * cosX2ToTadian * mulSinDelLng);

        distance = 2 * radius * Math.asin(squareRoot);
        log.info("distance: {}", distance);

        return BigDecimal.valueOf(distance);
    }
}
