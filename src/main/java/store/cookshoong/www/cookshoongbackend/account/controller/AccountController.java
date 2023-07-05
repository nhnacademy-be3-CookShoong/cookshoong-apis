package store.cookshoong.www.cookshoongbackend.account.controller;

import javax.validation.Valid;
import javax.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.exception.AuthorityNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.exception.SignUpValidationException;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.vo.AuthorityCode;
import store.cookshoong.www.cookshoongbackend.account.service.AccountService;

/**
 * 회원가입, 회원 조회, 회원 관련 정보 수정를 다루는 컨트롤러.
 *
 * @author koesnam
 * @since 2023.07.05
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    /**
     * 회원 가입처리 메서드.
     *
     * @param authorityCode  ex) customer, business
     * @param signUpRequestDto 회원가입 Dto
     * @return 201
     */
    @PostMapping("{authorityCode}")
    public ResponseEntity<Void> registerAccount(@Valid SignUpRequestDto signUpRequestDto, BindingResult bindingResult,
                                                @PathVariable String authorityCode) {

        if (AuthorityCode.matches(authorityCode.toUpperCase())) {
            throw new AuthorityNotFoundException(authorityCode.toUpperCase());
        }

        if (bindingResult.hasErrors()) {
            throw new SignUpValidationException(bindingResult);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
            .build();
    }
}
