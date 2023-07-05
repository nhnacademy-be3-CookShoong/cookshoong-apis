package store.cookshoong.www.cookshoongbackend.address.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.address.model.request.CreateAccountAddressRequestDto;
import store.cookshoong.www.cookshoongbackend.address.model.request.ModifyAccountAddressRequestDto;
import store.cookshoong.www.cookshoongbackend.address.model.response.AccountAddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.model.response.AddressResponseDto;
import store.cookshoong.www.cookshoongbackend.address.service.AddressService;

/**
 * 주소 RestController.
 * 주소에 대한 생성, 수정, 삭제, 조회를 담당한다.
 * 회원이 가지고 있는 주소를 모두 조회 가능하며, 회원은 최대 10까지에 주소를 등록 가능하다.
 *
 * @author jeongjewan
 * @since 2023.07.05
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    /**
     * 회원의 아이디와 주소를 생성해주는 Controller.
     *
     * @param accountId     회원 아이디
     * @param requestDto    회원이 등록하고자 하는 주소
     * @return              상태코드 201(CREATED)와 함께 응답을 반환
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<Void> createAccountAddress(@PathVariable("accountId") Long accountId,
                                                     @RequestBody CreateAccountAddressRequestDto requestDto) {

        addressService.createAccountAddress(accountId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 회원의 상세 주소를 변경하는 Controller.
     *
     * @param accountId     회원 아이디
     * @param addressId     주소 아이디
     * @param requestDto    회원이 수정하고자 하는 상세 주소
     * @return              상태코드 200(Ok)와 함께 응답을 반환
     */
    @PatchMapping("/{accountId}/{addressId}")
    public ResponseEntity<Void> modifyAccountDetailAddress(@PathVariable("accountId") Long accountId,
                                                           @PathVariable("addressId") Long addressId,
                                                           @RequestBody ModifyAccountAddressRequestDto requestDto) {

        addressService.modifyAccountDetailAddress(accountId, addressId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 회원이 가지고 있는 모든 메인 주소와 별칭을 보여주는 Controller.
     *
     * @param accountId     회원 아이디
     * @return              상태코드 200(Ok)와 함께 응답을 반환 & 클라이언트에게 모든 별칭과 주소를 반환
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<List<AccountAddressResponseDto>> getAccountAddressList(@PathVariable Long accountId) {
        List<AccountAddressResponseDto> addressList = addressService.getAccountAddressList(accountId);
        return ResponseEntity.status(HttpStatus.OK).body(addressList);
    }

    /**
     * 회원이 주문하기 누른 후 결제 화면에서 보여주는 메인 주소와 상세 주소 Controller.
     *
     * @param accountId     회원 아이디
     * @param addressId     주소 아이디
     * @return              상태코드 200(Ok)와 함께 응답을 반환 & 클라이언트에게 해당 메인 주소와 상세 주소를 반환
     */
    @GetMapping("/{accountId}/{addressId}")
    public ResponseEntity<AddressResponseDto> getAccountAddressForPayment(@PathVariable("accountId") Long accountId,
                                                                          @PathVariable("addressId") Long addressId) {

        AddressResponseDto address = addressService.getAccountAddressForPayment(accountId, addressId);

        return ResponseEntity.status(HttpStatus.OK).body(address);
    }

    /**
     * 회원이 지정한 특정 주소를 삭제하는 Controller.
     *
     * @param accountId     회원 아이디
     * @param addressId     주소 아이디
     * @return              상태코드 200(Ok)와 함께 응답을 반환
     */
    @DeleteMapping("/{accountId}/{addressId}")
    public ResponseEntity<Void> deleteAccountAddress(@PathVariable("accountId") Long accountId,
                                                     @PathVariable("addressId") Long addressId) {

        addressService.deleteAccountAddress(accountId, addressId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}


