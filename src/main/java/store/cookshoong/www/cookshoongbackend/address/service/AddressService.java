package store.cookshoong.www.cookshoongbackend.address.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.cookshoong.www.cookshoongbackend.address.repository.accountaddress.AccountAddressRepository;
import store.cookshoong.www.cookshoongbackend.address.repository.address.AddressRepository;

/**
 * 주소 서비스를 구현하는 Service.
 *
 * @author jeongjewan
 * @since 2023.07.04
 */
@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    private final AccountAddressRepository accountAddressRepository;



}
