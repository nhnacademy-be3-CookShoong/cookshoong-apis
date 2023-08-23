package store.cookshoong.www.cookshoongbackend.shop.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
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
import store.cookshoong.www.cookshoongbackend.address.service.AddressService;
import store.cookshoong.www.cookshoongbackend.file.ImageNotFoundException;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.model.ThumbnailManager;
import store.cookshoong.www.cookshoongbackend.file.repository.ImageRepository;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtilResolver;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtils;
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
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreInfoRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateStoreManagerRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresNotOutedResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreCategoriesDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.bank.BankTypeRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.category.StoreCategoryRepository;
import store.cookshoong.www.cookshoongbackend.shop.repository.category.StoresHasCategoryRepository;
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
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final EntityManager entityManager;
    private final StoreRepository storeRepository;
    private final BankTypeRepository bankTypeRepository;
    private final AccountRepository accountRepository;
    private final MerchantRepository merchantRepository;
    private final StoreStatusRepository storeStatusRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final AccountAddressRepository accountAddressRepository;
    private final FileUtilResolver fileUtilResolver;
    private final ImageRepository imageRepository;
    private final AddressService addressService;
    private final ThumbnailManager thumbnailManager;
    private final StoresHasCategoryRepository storesHasCategoryRepository;

    private static final Long BASIC_IMAGE = 1L;
    private static final BigDecimal DISTANCE = new BigDecimal("3.0");
    private static final Double RADIUS = 6371.0;
    private static final Double TO_RADIAN = Math.PI / 180;

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

    private Image storeFile(FileUtils fileUtils, Map<String, MultipartFile> fileMap, String domainName, boolean isPublic) throws IOException {
        MultipartFile multipartFile = fileMap.get(domainName);
        if (Objects.isNull(multipartFile) || multipartFile.isEmpty()) {
            return null;
        }
        return fileUtils.storeFile(multipartFile, domainName, isPublic);
    }

    private Merchant getMerchantById(Long merchantId) {
        if (Objects.isNull(merchantId)) {
            return null;
        }
        return merchantRepository.findById(merchantId)
            .orElseThrow(MerchantNotFoundException::new);
    }

    private Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(UserNotFoundException::new);
    }

    private BankType getBankTypeByCode(String bankCode) {
        return bankTypeRepository.findById(bankCode)
            .orElseThrow(BankTypeNotFoundException::new);
    }

    private StoreStatus getStoreStatusByCode(String code) {
        return storeStatusRepository.findById(code)
            .orElseThrow(StoreStatusNotFoundException::new);
    }

    private Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);
    }

    /**
     * 사업자 회원 : 매장을 pagination 으로 작성.
     *
     * @param accountId 회원아이디
     * @return the page
     */
    @Transactional(readOnly = true)
    public List<SelectAllStoresResponseDto> selectAllStores(Long accountId) {
        return storeRepository.lookupStores(accountId);
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
        Store store = getStoreById(storeId);
        accessDeniedException(accountId, store);
        SelectStoreResponseDto responseDto = storeRepository.lookupStore(accountId, storeId)
            .orElseThrow(StoreNotFoundException::new);
        List<SelectStoreCategoriesDto> categories = storeRepository.lookupStoreCategories(storeId);
        responseDto.setStoreCategories(categories);
        String domainName = responseDto.getDomainName();
        long storeImageId = store.getStoreImage().getId();
        if (BASIC_IMAGE == storeImageId) {
            domainName = thumbnailManager.getThumbnailDomain(responseDto.getDomainName());
        }
        FileUtils fileUtils = fileUtilResolver.getFileService(responseDto.getLocationType());
        responseDto.setPathName(fileUtils.getFullPath(domainName, responseDto.getPathName()));
        return responseDto;
    }

    /**
     * 일반 유저 : 매장 정보 조회.
     *
     * @param addressId the address id
     * @param storeId   매장 아이디
     * @return 매장 정보 조회
     */
    public SelectStoreForUserResponseDto selectStoreForUser(Long addressId, Long storeId) {
        SelectStoreForUserResponseDto responseDto = storeRepository.lookupStoreForUser(storeId)
            .orElseThrow(StoreNotFoundException::new);
        FileUtils fileUtils = fileUtilResolver.getFileService(responseDto.getLocationType());
        responseDto.setSavedName(fileUtils.getFullPath(responseDto.getDomainName(), responseDto.getSavedName()));

        Store store = storeRepository.findById(storeId).orElseThrow(StoreNotFoundException::new);

        BigDecimal distance = calculateDistance(
            addressService.selectAccountChoiceAddress(addressId).getLatitude(),
            addressService.selectAccountChoiceAddress(addressId).getLongitude(),
            addressService.selectAccountChoiceAddress(store.getAddress().getId()).getLatitude(),
            addressService.selectAccountChoiceAddress(store.getAddress().getId()).getLongitude()
        );

        int meterDistance = (int) (distance.doubleValue() * 1000);
        responseDto.setDistance(meterDistance);
        Integer extraDeliveryCost = (meterDistance / 1000) * 1000;
        responseDto.setTotalDeliveryCost(store.getDeliveryCost() + extraDeliveryCost);
        Integer deliveryTime = 10 + (meterDistance / 500) * 5;
        responseDto.setDeliveryTime(deliveryTime);

        return responseDto;
    }

    /**
     * 사업자 : 매장 등록 서비스 구현.
     * 가맹점 등록시 찾아서 넣고, 없으면 null로 등록, 매장 등록시 바로 CLOSE(휴식중) 상태로 등록됨.
     *
     * @param accountId          회원 아이디
     * @param registerRequestDto 매장 등록을 위한 정보
     * @param storedAt           the stored at
     * @param fileMap            the file map
     * @return the long
     * @throws IOException the io exception
     */
    public Long createStore(Long accountId, CreateStoreRequestDto registerRequestDto, String storedAt,
                            Map<String, MultipartFile> fileMap) throws IOException {
        if (storeRepository.existsStoreByBusinessLicenseNumber(registerRequestDto.getBusinessLicenseNumber())) {
            throw new DuplicatedBusinessLicenseException(registerRequestDto.getBusinessLicenseNumber());
        }
        Merchant merchant = getMerchantById(registerRequestDto.getMerchantId());
        Account account = getAccountById(accountId);
        BankType bankType = getBankTypeByCode(registerRequestDto.getBankCode());

        StoreStatus storeStatus = storeStatusRepository.getReferenceById(StoreStatus.StoreStatusCode.CLOSE.name());

        FileUtils fileUtils = fileUtilResolver.getFileService(storedAt);
        Image businessLicenseImage = storeFile(fileUtils, fileMap, FileDomain.BUSINESS_INFO_IMAGE.getVariable(), false);
        Image storeMainImage = imageRepository.findById(BASIC_IMAGE).orElseThrow((ImageNotFoundException::new));
        if (fileMap.containsKey(FileDomain.STORE_IMAGE.getVariable())) {
            storeMainImage = storeFile(fileUtils, fileMap, FileDomain.STORE_IMAGE.getVariable(), true);
        }
        Store store = new Store(merchant,
            account,
            bankType,
            storeStatus,
            businessLicenseImage,
            registerRequestDto,
            storeMainImage);

        List<String> categories = registerRequestDto.getStoreCategories();
        addStoreCategory(categories, store);

        Address address = new Address(registerRequestDto.getMainPlace(), registerRequestDto.getDetailPlace(),
            registerRequestDto.getLatitude(), registerRequestDto.getLongitude());
        store.modifyAddress(address);

        return storeRepository.save(store).getId();
    }

    /**
     * 사업자 : 매장 정보 수정 (사업자 정보).
     *
     * @param accountId  the account id
     * @param storeId    the store id
     * @param requestDto 매장 수정 정보
     * @throws IOException the io exception
     */
    public void updateStore(Long accountId, Long storeId, UpdateStoreManagerRequestDto requestDto) {
        Store store = getStoreById(storeId);
        accessDeniedException(accountId, store);
        BankType bankType = getBankTypeByCode(requestDto.getBankCode());

        store.modifyStore(bankType, requestDto);

    }

    /**
     * 사업자 : 매장 정보 수정 (영업점 정보).
     *
     * @param accountId  the account id
     * @param storeId    the store id
     * @param requestDto the request dto
     */
    public void updateStoreInfo(Long accountId, Long storeId, UpdateStoreInfoRequestDto requestDto) {
        Store store = getStoreById(storeId);
        accessDeniedException(accountId, store);

        store.modifyStoreInformation(requestDto);

        Address address = new Address(requestDto.getMainPlace(), requestDto.getDetailPlace(),
            requestDto.getLatitude(), requestDto.getLongitude());
        store.modifyAddress(address);
    }

    /**
     * 매장 사진을 수정하기 위한 서비스 코드.
     *
     * @param accountId  the account id
     * @param storeId    the store id
     * @param storeImage the store image
     * @throws IOException the io exception
     */
    public void updateStoreImage(Long accountId, Long storeId, MultipartFile storeImage) throws IOException {
        Store store = getStoreById(storeId);
        accessDeniedException(accountId, store);
        Image storeMainImage = imageRepository.findById(store.getStoreImage().getId()).orElseThrow((ImageNotFoundException::new));

        Image updatedImage = updateImage(store.getStoreImage().getId(), storeImage, storeMainImage);
        store.modifyStoreImage(updatedImage);
    }

    private Image updateImage(Long storeImageId, MultipartFile storeImage, Image storeMainImage) throws IOException {
        FileUtils fileUtils = fileUtilResolver.getFileService(storeMainImage.getLocationType());
        if (storeImageId.equals(BASIC_IMAGE)) {
            return fileUtils.storeFile(storeImage, FileDomain.STORE_IMAGE.getVariable(), true);
        }
        fileUtils.deleteFile(storeMainImage);
        imageRepository.deleteById(storeImageId);
        return fileUtils.storeFile(storeImage, FileDomain.STORE_IMAGE.getVariable(), true);
    }

    /**
     * 사업자 : 매장 카테고리 수정.
     *
     * @param accountId  the account id
     * @param storeId    the store id
     * @param requestDto 매장 카테고리 code list
     */
    public void updateStoreCategories(Long accountId, Long storeId, UpdateCategoryRequestDto requestDto) {
        Store store = getStoreById(storeId);
        accessDeniedException(accountId, store);

        storesHasCategoryRepository.deleteAllByPkStoreId(storeId);
        store.initStoreCategories();
        entityManager.flush();
        addStoreCategory(requestDto.getUpdateStoreCategories(), store);
    }

    /**
     * 사업자 : 매장 상태 변경.
     *
     * @param accountId the account id
     * @param storeId   the store id
     * @param option    the option
     */
    public void updateStoreStatus(Long accountId, Long storeId, String option) {
        Store store = getStoreById(storeId);
        accessDeniedException(accountId, store);
        if (StoreStatus.StoreStatusCode.OUTED.name().equals(store.getStoreStatus().getCode())) {
            throw new UserAccessDeniedException("해당 매장 상태 변경을 할 수 있는 권한이 없습니다.");
        }
        StoreStatus storeStatus = getStoreStatusByCode(option);
        store.modifyStoreStatus(storeStatus);
    }

    /**
     * 사업자 : 매장 이미지 삭제.
     *
     * @param accountId the account id
     * @param storeId   the store id
     * @throws IOException the io exception
     */
    public void deleteStoreImage(Long accountId, Long storeId) throws IOException {
        Store store = getStoreById(storeId);
        accessDeniedException(accountId, store);

        if (store.getStoreImage().getId().equals(BASIC_IMAGE)) {
            throw new UserAccessDeniedException("삭제할 수 있는 권한이 없습니다.");
        }

        FileUtils fileUtils = fileUtilResolver.getFileService(store.getStoreImage().getLocationType());
        fileUtils.deleteFile(store.getStoreImage());

        imageRepository.deleteById(store.getStoreImage().getId());

        Image image = imageRepository.findById(BASIC_IMAGE).orElseThrow((ImageNotFoundException::new));
        store.modifyStoreImage(image);
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
            .filter(store -> isInStandardDistance(addressLatLng, store))
            .collect(Collectors.toList());

        for (SelectAllStoresNotOutedResponseDto dto : nearbyStores) {
            if (Objects.nonNull(dto.getSavedName())) {
                FileUtils fileUtils = fileUtilResolver.getFileService(dto.getLocationType());
                dto.setSavedName(
                    fileUtils.getFullPath(dto.getDomainName(), dto.getSavedName()));
            }
        }

        return new PageImpl<>(nearbyStores, pageable, nearbyStores.size());
    }

    /**
     * Is in standard distance boolean.
     *
     * @param accountAddress the account address
     * @param storeId        the store id
     * @return the boolean
     */
    public boolean isInStandardDistance(AddressResponseDto accountAddress, Long storeId) {
        Store store = storeRepository.findById(storeId)
            .orElseThrow(StoreNotFoundException::new);

        Address storeAddress = store.getAddress();

        return isInStandardDistance(
            accountAddress.getLatitude(), accountAddress.getLongitude(),
            storeAddress.getLatitude(), storeAddress.getLongitude());
    }

    private boolean isInStandardDistance(AddressResponseDto address, SelectAllStoresNotOutedResponseDto store) {
        return isInStandardDistance(
            address.getLatitude(), address.getLongitude(),
            store.getLatitude(), store.getLongitude()
        );
    }

    private boolean isInStandardDistance(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2) {
        BigDecimal storeDistance = calculateDistance(x1, y1, x2, y2);
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

    @Transactional(readOnly = true)
    public int selectStoreDeliveryCost(Long storeId) {
        return getStoreById(storeId).getDeliveryCost();
    }
}
