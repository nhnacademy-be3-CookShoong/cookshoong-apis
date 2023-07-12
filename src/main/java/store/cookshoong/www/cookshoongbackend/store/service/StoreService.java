package store.cookshoong.www.cookshoongbackend.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.store.entity.BankType;
import store.cookshoong.www.cookshoongbackend.store.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;
import store.cookshoong.www.cookshoongbackend.store.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.store.exception.banktype.BankTypeNotFoundException;
import store.cookshoong.www.cookshoongbackend.store.exception.store.DuplicatedBusinessLicenseException;
import store.cookshoong.www.cookshoongbackend.store.exception.store.SelectStoreNotFoundException;
import store.cookshoong.www.cookshoongbackend.store.model.request.CreateStoreRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllStoresResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectStoreForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.store.repository.bank.BankTypeRepository;
import store.cookshoong.www.cookshoongbackend.store.repository.merchant.MerchantRepository;
import store.cookshoong.www.cookshoongbackend.store.repository.store.StoreRepository;
import store.cookshoong.www.cookshoongbackend.store.repository.store.StoreStatusRepository;

/**
 * 매장리스트 조회, 등록, 삭제, 수정 서비스 구현.
 *
 * @author seungyeon
 * @since 2023.07.05
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StoreService {
    private final StoreRepository storeRepository;
    private final BankTypeRepository bankTypeRepository;
    private final AccountRepository accountRepository;
    private final MerchantRepository merchantRepository;
    private final StoreStatusRepository storeStatusRepository;

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

        Merchant merchant = merchantRepository.findMerchantByName(registerRequestDto.getMerchantName()).orElse(null);
        Account account = accountRepository.findById(accountId)
            .orElseThrow(UserNotFoundException::new);
        BankType bankType = bankTypeRepository.findByDescription(registerRequestDto.getBankName())
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

        Address address = new Address(registerRequestDto.getMainPlace(), registerRequestDto.getDetailPlace(),
            registerRequestDto.getLatitude(), registerRequestDto.getLongitude());
        store.modifyAddress(address);

        storeRepository.save(store);
    }

    //TODO 5. 매장 수정 -> 영업시간, 휴무일 후에 다시 작업, 삭제는 없고, 상태변경으로 폐업시킴
}
