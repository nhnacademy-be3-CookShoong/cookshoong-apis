package store.cookshoong.www.cookshoongbackend.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.store.entity.BankType;
import store.cookshoong.www.cookshoongbackend.store.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.store.entity.Store;
import store.cookshoong.www.cookshoongbackend.store.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.store.model.request.StoreRegisterRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.StoreListResponseDto;
import store.cookshoong.www.cookshoongbackend.store.repository.BankTypeRepository;
import store.cookshoong.www.cookshoongbackend.store.repository.MerchantRepository;
import store.cookshoong.www.cookshoongbackend.store.repository.StoreRepository;
import store.cookshoong.www.cookshoongbackend.store.repository.StoreStatusRepository;

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
     * 해당 회원의 매장을 pagination 으로 작성.
     *
     * @param accountId 회원아이디
     * @param pageable  페이지 정보
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<StoreListResponseDto> selectStoreList(Long accountId, Pageable pageable) {
        return storeRepository.lookupStoresPage(accountId, pageable);
    }

    /**
     * 매장 등록 서비스 구현.
     * 가맹점 등록시 찾아서 넣고, 없으면 null로 등록, 매장 등록시 바로 CLOSE(휴식중) 상태로 등록됨.
     *
     * @param registerRequestDto 매장 등록을 위한 정보
     */
    public void createStore(StoreRegisterRequestDto registerRequestDto) {
        if (storeRepository.existsStoreByBusinessLicenseNumber(registerRequestDto.getBusinessLicense())) {
            throw new IllegalArgumentException("이미 등록된 사업장입니다.");
        }
        // TODO 7. 회원 정보에 대한 부분은 나중에 수정
        // TODO 9. Exception 처리 한꺼번에 수정

        Merchant merchant = merchantRepository.findMerchantByName(registerRequestDto.getMerchantName()).orElse(null);
        Account account = accountRepository.findById(4L).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        BankType bankType = bankTypeRepository.findBankTypeByDescription(registerRequestDto.getBankType()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 은행타입입니다."));
        StoreStatus storeStatus = storeStatusRepository.findById("CLOSE").orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상태입니다."));

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

        // TODO 8. 주소 나중에 다시 확인 일단 Dto로 담아서 저장시킴
        Address address = new Address(registerRequestDto.getMainPlace(), registerRequestDto.getDetailPlace(),
            registerRequestDto.getLatitude(), registerRequestDto.getLongitude());
        store.modifyAddress(address);

        storeRepository.save(store);
    }


    //TODO 5. 수정

    /**
     * 매장 삭제 구현.
     *
     * @param storeId 매장 아이디
     */
    public void removeStore(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new IllegalArgumentException("존재하지 않는 가게입니다.");
        }
        storeRepository.deleteById(storeId);
    }

}
