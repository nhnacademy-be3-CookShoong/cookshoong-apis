package store.cookshoong.www.cookshoongbackend.address.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.address.entity.AccountAddress;
import store.cookshoong.www.cookshoongbackend.address.entity.Address;
import store.cookshoong.www.cookshoongbackend.address.exception.AccountAddressNotFoundException;
import store.cookshoong.www.cookshoongbackend.address.exception.MaxAddressLimitException;
import store.cookshoong.www.cookshoongbackend.address.model.request.CreateAccountAddressRequestDto;
import store.cookshoong.www.cookshoongbackend.address.model.request.ModifyAccountAddressRequestDto;
import store.cookshoong.www.cookshoongbackend.address.model.response.AccountAddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.model.response.AddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.repository.accountaddress.AccountAddressRepository;
import store.cookshoong.www.cookshoongbackend.address.repository.address.AddressRepository;

/**
 * 회원과 주소 서비스를 구현하는 AccountAddressService.
 *
 * @author jeongjewan
 * @since 2023.07.05
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private static final int MAX_ADDRESS_COUNT = 10;

    private final AccountRepository accountRepository;
    private final AccountAddressRepository accountAddressRepository;
    private final AddressRepository addressRepository;

    /**
     * 회원이 별칭과 주소를 등록하는 메서드.
     *
     * @param accountId     회원 아이디
     * @param requestDto    회원에 의해 생성되는 별칭과 주소
     */
    public void createAccountAddress(Long accountId, CreateAccountAddressRequestDto requestDto) {

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new IllegalArgumentException("계정이 존재하지 않습니다."));

        List<AccountAddress> accountAddressList = accountAddressRepository.findByAccount(account);
        if (accountAddressList.size() >= MAX_ADDRESS_COUNT) {
            throw new MaxAddressLimitException();
        }

        Address address = new Address(requestDto.getMainPlace(), requestDto.getDetailPlace(),
            requestDto.getLatitude(), requestDto.getLongitude());

        addressRepository.save(address);

        AccountAddress accountAddress = new AccountAddress(new AccountAddress.Pk(accountId, address.getId()),
            account, address, requestDto.getAlias());

        accountAddressRepository.save(accountAddress);
    }

    /**
     * 회원이 주문하기를 누른 후 결제화면에서 상세주소를 변경할 때 사용되는 메서드.
     *
     * @param accountId     회원 아이디
     * @param addressId     주소 아이디
     * @param requestDto    상세주소를 가지고 Dto
     */
    public void modifyAccountDetailAddress(Long accountId, Long addressId, ModifyAccountAddressRequestDto requestDto) {

        AccountAddress.Pk pk = new AccountAddress.Pk(accountId, addressId);

        AccountAddress accountAddress = accountAddressRepository.findById(pk)
            .orElseThrow(AccountAddressNotFoundException::new);

        accountAddress.getAddress().updateDetailAddress(requestDto);
        accountAddressRepository.save(accountAddress);
    }

    /**
     * 회원이 등록한 주소 리스트를 보여주는 메서드.
     *
     * @param accountId     회원 아이디
     * @return              회원이 등록한 주소 중 메인 주소만 리턴
     */
    public List<AccountAddressResponseDto> getAccountAddressList(Long accountId) {

        return accountAddressRepository.getByAccountIdAddress(accountId);
    }

    /**
     * 회원이 주문하기를 누르고 결제페이지에서 보여주는 메인 주소와 상세 주소.
     *
     * @param accountId     회원 아이디
     * @return              회원의 결제 페이지에서 보여주는 메인 주소와 상세 주소 리턴
     */
    public AddressResponseDto getAccountAddressForPayment(Long accountId, Long addressId) {

        AccountAddress.Pk pk = new AccountAddress.Pk(accountId, addressId);

        AccountAddress accountAddress = accountAddressRepository.findById(pk)
            .orElseThrow(AccountAddressNotFoundException::new);

        String mainPlace = accountAddress.getAddress().getMainPlace();
        String detailPlace = accountAddress.getAddress().getDetailPlace();

        return new AddressResponseDto(mainPlace, detailPlace);
    }


    /**
     * 회원이 지정한 해당 주소를 삭제하는 메서드.
     *
     * @param accountId     회원 아이디
     * @param addressId     주소 아이디
     */
    public void deleteAccountAddress(Long accountId, Long addressId) {

        AccountAddress.Pk pk = new AccountAddress.Pk(accountId, addressId);

        AccountAddress accountAddress = accountAddressRepository.findById(pk)
            .orElseThrow(AccountAddressNotFoundException::new);

        accountAddressRepository.delete(accountAddress);
    }

}
