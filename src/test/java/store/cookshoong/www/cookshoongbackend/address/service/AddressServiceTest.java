package store.cookshoong.www.cookshoongbackend.address.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
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
 * 주소 서비스에 대한 테스트.
 *
 * @author jeongjewan
 * @since 2023.07.13
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressService addressService;

    @Mock
    private AccountAddressRepository accountAddressRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AccountRepository accountRepository;

    static SignUpRequestDto testDto;
    static Authority testAuthority;
    static AccountStatus testAccountStatus;
    static Rank testRank;
    static Account testAccount;
    static AccountAddress accountAddress;
    static Address testAddress;

    @BeforeEach
    void setup() {
        testDto = ReflectionUtils.newInstance(SignUpRequestDto.class);
        ReflectionTestUtils.setField(testDto, "loginId", "user1");
        ReflectionTestUtils.setField(testDto, "password", "1234");
        ReflectionTestUtils.setField(testDto, "name", "유유저");
        ReflectionTestUtils.setField(testDto, "nickname", "이름이유저래");
        ReflectionTestUtils.setField(testDto, "email", "user@cookshoong.store");
        ReflectionTestUtils.setField(testDto, "birthday", LocalDate.of(1997, 6, 4));
        ReflectionTestUtils.setField(testDto, "phoneNumber", "01012345678");

        testAuthority = new Authority("CUSTOMER", "일반 회원");
        testAccountStatus = new AccountStatus("ACTIVE", "활성");
        testRank = new Rank("LEVEL_1", "프랜드");

        testAccount = new Account(testAccountStatus, testAuthority, testRank, testDto);
        ReflectionTestUtils.setField(testAccount, "id", 1L);

        testAddress = new Address("mainPlaceT", "detailPlaceT",
            new BigDecimal("23.2323223"), new BigDecimal("24.12312312"));

        accountAddress = new AccountAddress(new AccountAddress.Pk(testAccount.getId(), testAddress.getId()),
            testAccount, testAddress, "alias", LocalDateTime.now());
    }

    @Test
    @DisplayName("회원이 주소를 등록")
    void createAccountAddress() {
        CreateAccountAddressRequestDto requestDto = ReflectionUtils.newInstance(CreateAccountAddressRequestDto.class);

        ReflectionTestUtils.setField(requestDto, "mainPlace", "mainPlace");
        ReflectionTestUtils.setField(requestDto, "detailPlace", "detailPlace");
        ReflectionTestUtils.setField(requestDto, "latitude", new BigDecimal("23.323242342"));
        ReflectionTestUtils.setField(requestDto, "longitude", new BigDecimal("24.23552423"));

        List<AccountAddress> accountAddresses = new ArrayList<>();

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(accountAddressRepository.findByAccount(testAccount)).thenReturn(accountAddresses);

        addressService.createAccountAddress(testAccount.getId(), requestDto);


        verify(accountRepository, times(1)).findById(testAccount.getId());
        verify(accountAddressRepository, times(1)).findByAccount(testAccount);
        verify(addressRepository, times(1)).save(any(Address.class));
        verify(accountAddressRepository, times(1)).save(any(AccountAddress.class));
    }

    @Test
    @DisplayName("회원이 주소를 등록 실패: 회원이 가지고 있는 주소가 10개 초과될 때")
    void createAccountAddress_MaxAddressLimitedReached_Throw() {
        CreateAccountAddressRequestDto requestDto = ReflectionUtils.newInstance(CreateAccountAddressRequestDto.class);

        ReflectionTestUtils.setField(requestDto, "mainPlace", "mainPlace");
        ReflectionTestUtils.setField(requestDto, "detailPlace", "detailPlace");
        ReflectionTestUtils.setField(requestDto, "latitude", new BigDecimal("23.323242342"));
        ReflectionTestUtils.setField(requestDto, "longitude", new BigDecimal("24.23552423"));

        List<AccountAddress> accountAddresses = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            accountAddresses.add(accountAddress);
        }

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(accountAddressRepository.findByAccount(testAccount)).thenReturn(accountAddresses);

        Long testAccountId = testAccount.getId();
        assertThrows(MaxAddressLimitException.class,
            () -> addressService.createAccountAddress(testAccountId, requestDto));

        verify(accountRepository, times(1)).findById(testAccount.getId());
        verify(accountAddressRepository, times(1)).findByAccount(testAccount);
        verify(addressRepository, never()).save(any(Address.class));
        verify(accountAddressRepository, never()).save(any(AccountAddress.class));
    }

    @Test
    @DisplayName("회원이 주문할 때 상세 주소 수정")
    void updateAccountDetailAddress() {

        ModifyAccountAddressRequestDto requestDto = ReflectionUtils.newInstance(ModifyAccountAddressRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "detailPlace", "test Detail Place");

        AccountAddress.Pk pk = new AccountAddress.Pk(testAccount.getId(), testAddress.getId());

        when(accountAddressRepository.findById(pk)).thenReturn(Optional.of(accountAddress));

        addressService.updateAccountDetailAddress(testAccount.getId(), testAddress.getId(), requestDto);

        verify(accountAddressRepository, times(1)).findById(pk);

        assertEquals(requestDto.getDetailPlace(), accountAddress.getAddress().getDetailPlace());
    }

    @Test
    @DisplayName("회원이 주문할 때 상세 주소 수정 실패: 해당 주소가 존재하지 않을 때")
    void updateAccountDetailAddress_NotFound_Address_Throw() {

        ModifyAccountAddressRequestDto requestDto = ReflectionUtils.newInstance(ModifyAccountAddressRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "detailPlace", "test Detail Place");

        AccountAddress.Pk pk = new AccountAddress.Pk(testAccount.getId(), testAddress.getId());

        when(accountAddressRepository.findById(pk)).thenReturn(Optional.empty());

        Long testAccountId = testAccount.getId();
        Long testAddressId = testAddress.getId();

        assertThrows(AccountAddressNotFoundException.class,
            () -> addressService.updateAccountDetailAddress(testAccountId, testAddressId, requestDto));

        verify(accountAddressRepository, times(1)).findById(pk);
    }

    @Test
    @DisplayName("회원이 선택한 주소에 대해 갱신 날짜를 업데이트")
    void updateSelectAccountAddressRenewalAt() {

        AccountAddress.Pk pk = new AccountAddress.Pk(testAccount.getId(), testAddress.getId());

        when(accountAddressRepository.findById(pk)).thenReturn(Optional.of(accountAddress));

        addressService.updateSelectAccountAddressRenewalAt(testAccount.getId(), testAddress.getId());

        verify(accountAddressRepository, times(1)).findById(pk);
    }

    @Test
    @DisplayName("회원이 선택한 주소에 대해 갱신 날짜를 업데이트 실패: 해당 주소가 존재하지 않을 때")
    void updateSelectAccountAddressRenewalAt_NotFound_Address_Throw() {

        AccountAddress.Pk pk = new AccountAddress.Pk(testAccount.getId(), testAddress.getId());

        when(accountAddressRepository.findById(pk)).thenReturn(Optional.empty());

        Long testAccountId = testAccount.getId();
        Long testAddressId = testAddress.getId();
        assertThrows(AccountAddressNotFoundException.class,
            () -> addressService.updateSelectAccountAddressRenewalAt(testAccountId, testAddressId));

        verify(accountAddressRepository, times(1)).findById(pk);
    }


    @Test
    @DisplayName("회원이 가지고 있는 모든 주소 가져오기, 별칭과 메인주소만")
    void selectAccountAddressList() {

        List<AccountAddressResponseDto> addressResponses = new ArrayList<>();
        addressResponses.add(new AccountAddressResponseDto(testAccount.getId(), "alias", "성수동"));

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(accountAddressRepository.lookupByAccountIdAddressAll(testAccount.getId())).thenReturn(addressResponses);

        List<AccountAddressResponseDto> actual = addressService.selectAccountAddressList(testAccount.getId());

        verify(accountRepository, times(1)).findById(testAccount.getId());
        verify(accountAddressRepository, times(1)).lookupByAccountIdAddressAll(testAccount.getId());
        assertNotNull(actual);
        assertEquals(addressResponses, actual);
        assertEquals(addressResponses.get(0).getMainPlace(), actual.get(0).getMainPlace());
    }

    @Test
    @DisplayName("회원이 가지고 있는 모든 주소 가져오기, 별칭과 메인주소만 실패 - 회원이 존재하지 않을 때")
    void selectAccountAddressList_NotFound_Account_Throw() {

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.empty());

        Long testAccountId = testAccount.getId();

        assertThrows(UserNotFoundException.class,
            () -> addressService.selectAccountAddressList(testAccountId));

        verify(accountRepository, times(1)).findById(testAccount.getId());
    }

    @Test
    @DisplayName("회원이 최근에 갱신한 주소와 좌표 조회")
    void selectAccountAddressRecentRegistration() {

        AddressResponseDto expectedAddress = new AddressResponseDto(1L, "Main Place", "Detail Place",
            new BigDecimal("23.23233323"), new BigDecimal("23.232333255"));
        AddressResponseDto expectedAddress1 = new AddressResponseDto(1L, "Main Place", "Detail Place",
            new BigDecimal("23.23233323"), new BigDecimal("23.232333255"));

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.of(testAccount));
        when(accountAddressRepository.lookupByAccountAddressRenewalAt(testAccount.getId()))
            .thenReturn(expectedAddress, expectedAddress1);

        AddressResponseDto actual = addressService.selectAccountAddressRenewalAt(testAccount.getId());

        assertEquals(expectedAddress, actual);
        verify(accountRepository, times(1)).findById(testAccount.getId());
        verify(accountAddressRepository, times(1)).lookupByAccountAddressRenewalAt(testAccount.getId());

        assertEquals(actual.getMainPlace(), expectedAddress1.getMainPlace());
        assertEquals(actual.getDetailPlace(), expectedAddress1.getDetailPlace());
        assertEquals(actual.getLatitude(), expectedAddress1.getLatitude());
        assertEquals(actual.getLongitude(), expectedAddress1.getLongitude());
    }

    @Test
    @DisplayName("회원이 최근에 갱신한 주소와 좌표 조회 실패: 회원이 존재하지 않을 때")
    void selectAccountAddress_NotFound_Account_Throw() {

        when(accountRepository.findById(testAccount.getId())).thenReturn(Optional.empty());

        Long testAccountId = testAccount.getId();

        assertThrows(UserNotFoundException.class,
            () -> addressService.selectAccountAddressRenewalAt(testAccountId));

        verify(accountRepository, times(1)).findById(testAccount.getId());
    }

    @Test
    @DisplayName("회원이 주소록에서 선택한 주소 조회")
    void selectAccountChoiceAddress() {

        AddressResponseDto expectedAddress = new AddressResponseDto(1L, "Main Place1", "Detail Place1",
            new BigDecimal("23.23233323"), new BigDecimal("23.232333255"));
        AddressResponseDto expectedAddress1 = new AddressResponseDto(1L, "Main Place", "Detail Place",
            new BigDecimal("23.23233323"), new BigDecimal("23.232333255"));

        when(addressRepository.findById(testAddress.getId())).thenReturn(Optional.of(testAddress));
        when(accountAddressRepository.lookupByAccountSelectAddressId(testAddress.getId()))
            .thenReturn(expectedAddress1);

        AddressResponseDto actual = addressService.selectAccountChoiceAddress(testAddress.getId());

        assertEquals(expectedAddress1, actual);
        verify(addressRepository, times(1)).findById(testAddress.getId());
        verify(accountAddressRepository, times(1)).lookupByAccountSelectAddressId(testAddress.getId());

        assertEquals(actual.getMainPlace(), expectedAddress1.getMainPlace());
        assertEquals(actual.getDetailPlace(), expectedAddress1.getDetailPlace());
        assertEquals(actual.getLatitude(), expectedAddress1.getLatitude());
        assertEquals(actual.getLongitude(), expectedAddress1.getLongitude());
    }

    @Test
    @DisplayName("회원이 주소록에서 선택한 주소 조회 실패: 주소가 존재하지 않을 때")
    void selectAccountChoiceAddress_NotFound_Account_Throw() {

        when(addressRepository.findById(testAddress.getId())).thenReturn(Optional.empty());


        Long testAddressId = testAddress.getId();
        assertThrows(AccountAddressNotFoundException.class,
            () -> addressService.selectAccountChoiceAddress(testAddressId));

        verify(addressRepository, times(1)).findById(testAddress.getId());
    }

    @Test
    @DisplayName("회원이 주소를 삭제")
    void deleteAccountAddress() {

        AccountAddress.Pk pk = new AccountAddress.Pk(testAccount.getId(), testAddress.getId());

        when(accountAddressRepository.findById(pk)).thenReturn(Optional.of(accountAddress));

        addressService.deleteAccountAddress(testAccount.getId(), testAddress.getId());

        verify(accountAddressRepository, times(1)).findById(pk);
        verify(accountAddressRepository, times(1)).delete(accountAddress);
    }

    @Test
    @DisplayName("회원이 주소를 삭제 실패: 해당 주소가 존재하지 않을 때")
    void deleteAccountAddress_NotFound_Address_Throw() {

        AccountAddress.Pk pk = new AccountAddress.Pk(testAccount.getId(), testAddress.getId());

        when(accountAddressRepository.findById(pk)).thenReturn(Optional.empty());

        Long testAccountId = testAccount.getId();
        Long testAddressId = testAddress.getId();
        assertThrows(AccountAddressNotFoundException.class,
            () -> addressService.deleteAccountAddress(testAccountId, testAddressId));


        verify(accountAddressRepository, times(1)).findById(pk);
        verify(accountAddressRepository, never()).delete(accountAddress);
    }
}
