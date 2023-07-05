package store.cookshoong.www.cookshoongbackend.address.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.cookshoong.www.cookshoongbackend.address.repository.accountaddress.AccountAddressRepository;
import store.cookshoong.www.cookshoongbackend.address.repository.address.AddressRepository;

/**
 * {설명을 작성해주세요}.
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
