package store.cookshoong.www.cookshoongbackend.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllBanksResponseDto;
import store.cookshoong.www.cookshoongbackend.store.service.BankTypeService;

/**
 * 관리자 : 은행 타입 추가.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/banks")
public class BankController {
    private final BankTypeService bankTypeService;

    @GetMapping
    public ResponseEntity<Page<SelectAllBanksResponseDto>> getBanks(Pageable pageable) {
        return ResponseEntity
            .ok(bankTypeService.selectBanks(pageable));
    }
}
