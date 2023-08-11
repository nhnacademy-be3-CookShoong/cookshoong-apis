package store.cookshoong.www.cookshoongbackend.account.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllBanksResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllMerchantsResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.service.BankTypeService;
import store.cookshoong.www.cookshoongbackend.shop.service.MerchantService;
import store.cookshoong.www.cookshoongbackend.shop.service.StoreStatusService;

/**
 * 사업자 : 등록, 수정시 필요한 selectBox, checkBox 용 api.
 *
 * @author seungyeon
 * @since 2023.07.12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class BusinessSelectController {

    private final BankTypeService bankTypeService;
    private final MerchantService merchantService;
    private final StoreStatusService storeStatusService;

    /**
     * 사업자등록 : 은행 리스트 조회.
     *
     * @return 은행 리스트
     */
    @GetMapping("/banks")
    public ResponseEntity<List<SelectAllBanksResponseDto>> getBanksForUser() {
        return ResponseEntity
            .ok(bankTypeService.selectBanksForUser());
    }

    /**
     * 사업자 등록 : 가맹점 리스트 조회.
     *
     * @return 가맹점 리스트
     */
    @GetMapping("/merchants")
    public ResponseEntity<List<SelectAllMerchantsResponseDto>> getMerchantsForUser() {
        return ResponseEntity
            .ok(merchantService.selectAllMerchantsForUser());
    }

    @GetMapping("/store-status")
    public ResponseEntity<List<SelectAllStatusResponseDto>> getStoreStatusForUser() {
        return ResponseEntity
            .ok(storeStatusService.selectAllStatusForUser());
    }
}
