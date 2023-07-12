package store.cookshoong.www.cookshoongbackend.account.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllBanksForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllCategoriesForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllMerchantsForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.store.service.BankTypeService;
import store.cookshoong.www.cookshoongbackend.store.service.MerchantService;
import store.cookshoong.www.cookshoongbackend.store.service.StoreCategoryService;

/**
 * 사업자 : 등록, 수정시 필요한 selectBox, checkBox 용 api.
 *
 * @author seungyeon
 * @since 2023.07.12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts/")
public class BusinessSelectController {

    private final BankTypeService bankTypeService;
    private final MerchantService merchantService;
    private final StoreCategoryService storeCategoryService;

    @GetMapping("/banks")
    public ResponseEntity<List<SelectAllBanksForUserResponseDto>> getBanksForUser() {
        return ResponseEntity
            .ok(bankTypeService.selectBanksForUser());
    }

    @GetMapping("/merchants")
    public ResponseEntity<List<SelectAllMerchantsForUserResponseDto>> getMerchantsForUser() {
        return ResponseEntity
            .ok(merchantService.selectAllMerchantsForUser());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<SelectAllCategoriesForUserResponseDto>> getCategoriesForUser() {
        return ResponseEntity
            .ok(storeCategoryService.selectAllCategoriesForUser());
    }
}
