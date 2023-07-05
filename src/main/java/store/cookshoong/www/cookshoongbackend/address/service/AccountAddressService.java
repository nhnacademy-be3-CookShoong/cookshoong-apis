package store.cookshoong.www.cookshoongbackend.address.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.cookshoong.www.cookshoongbackend.address.repository.accountaddress.AccountAddressRepository;
import store.cookshoong.www.cookshoongbackend.address.repository.address.AddressRepository;

/**
 * 회원과 주소 서비스를 구현하는 AccountAddressService
 *
 * @author jeongjewan
 * @since 2023.07.05
 */
@Service
@RequiredArgsConstructor
public class AccountAddressService {

    private final AccountAddressRepository accountAddressRepository;
    private final AddressRepository addressRepository;


}
