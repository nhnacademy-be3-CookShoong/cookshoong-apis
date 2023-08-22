package store.cookshoong.www.cookshoongbackend.account.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.exception.AccountStatusNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.exception.AuthorityNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.exception.SignUpValidationException;
import store.cookshoong.www.cookshoongbackend.account.exception.UpdateAccountInfoValidationException;
import store.cookshoong.www.cookshoongbackend.account.model.request.OAuth2SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.request.UpdateAccountInfoRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountAuthResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountInfoResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.UpdateAccountStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.account.service.AccountService;
import store.cookshoong.www.cookshoongbackend.address.service.AddressService;
import store.cookshoong.www.cookshoongbackend.point.model.event.PointSignupEvent;

/**
 * 회원가입, 회원 조회, 회원 관련 정보 수정를 다루는 컨트롤러.
 *
 * @author koesnam (추만석)
 * @since 2023.07.05
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    private final AddressService addressService;
    private final ApplicationEventPublisher publisher;

    /**
     * 회원 정보를 전달받아 DB에 저장하게한다.
     *
     * @param authorityCode    ex) customer, business
     * @param signUpRequestDto 회원가입 Dto
     * @return 201
     */
    @PostMapping
    public ResponseEntity<Void> postAccount(@RequestBody @Valid SignUpRequestDto signUpRequestDto,
                                            BindingResult bindingResult,
                                            @RequestParam String authorityCode) {
        String authorityCodeUpperCase = authorityCode.toUpperCase();
        if (!Authority.Code.matches(authorityCodeUpperCase)) {
            throw new AuthorityNotFoundException();
        }

        if (bindingResult.hasErrors()) {
            throw new SignUpValidationException(bindingResult);
        }

        Long accountId = accountService.createAccount(signUpRequestDto, Authority.Code.valueOf(authorityCodeUpperCase));
        addressService.createAccountAddress(accountId, signUpRequestDto.getCreateAccountAddressRequestDto());

        publisher.publishEvent(new PointSignupEvent(this, accountId));

        return ResponseEntity.status(HttpStatus.CREATED)
            .build();
    }

    /**
     * OAuth 회원의 가입정보를 전달받아 DB에 저장하게한다. ( 일반 회원으로 가입을 강제한다. )
     *
     * @param oAuth2SignUpRequestDto the o auth 2 sign up request dto
     * @param bindingResult          the binding result
     * @return 201 response entity
     */
    @PostMapping("/oauth2")
    public ResponseEntity<Void> postOAuth2Account(@RequestBody @Valid OAuth2SignUpRequestDto oAuth2SignUpRequestDto,
                                                  BindingResult bindingResult
                                                  ) {
        if (bindingResult.hasErrors()) {
            throw new SignUpValidationException(bindingResult);
        }
        SignUpRequestDto signUpRequestDto = oAuth2SignUpRequestDto.getSignUpRequestDto();
        String accountCode = oAuth2SignUpRequestDto.getAccountCode();
        String provider = oAuth2SignUpRequestDto.getProvider();
        Long accountId = accountService.createAccount(signUpRequestDto, Authority.Code.CUSTOMER);
        addressService.createAccountAddress(accountId, signUpRequestDto.getCreateAccountAddressRequestDto());
        accountService.createOAuth2Account(accountId, accountCode, provider);

        return ResponseEntity.status(HttpStatus.CREATED)
            .build();
    }

    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    @GetMapping("/oauth2")
    public ResponseEntity<SelectAccountInfoResponseDto> getAccountInfoForOAuth(@RequestParam String provider,
                                                                               @RequestParam String accountCode) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(accountService.selectAccountInfoForOAuth(provider.toLowerCase(), accountCode));
    }

    /**
     * accountId 기준으로 회원의 모든 정보(password 제외)를 조회한다.
     *
     * @param accountId the account id
     * @return the account
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<SelectAccountResponseDto> getAccount(@PathVariable Long accountId) {
        SelectAccountResponseDto response = accountService.selectAccount(accountId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(response);
    }

    /**
     * 변경가능한 회원 정보를 수정한다.
     *
     * @param accountId the account id
     * @return the response entity
     */
    @PutMapping("/{accountId}")
    public ResponseEntity<Void> putAccount(@PathVariable Long accountId,
                                           @RequestBody @Valid UpdateAccountInfoRequestDto updateAccountInfoRequestDto,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new UpdateAccountInfoValidationException(bindingResult);
        }
        accountService.updateMutableAccountInfo(accountId, updateAccountInfoRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
            .build();
    }

    /**
     * 로그인 성공시 마지막 로그인 날짜를 업데이트한다.
     *
     * @param accountId the account id
     * @return the response entity
     */
    @PutMapping("/{accountId}/auth-success")
    public ResponseEntity<Void> putLoginLogging(@PathVariable Long accountId) {
        accountService.updateLastLoginDate(accountId);
        return ResponseEntity.status(HttpStatus.OK)
            .build();
    }

    /**
     * 로그인아이디를 경로로 받아와 자격증명에 필요한 정보를 조회한다.
     *
     * @param loginId the login id
     * @return the account for authentication
     */
    @GetMapping("/{loginId}/auth")
    public ResponseEntity<SelectAccountAuthResponseDto> getAccountForAuthentication(@PathVariable String loginId) {
        SelectAccountAuthResponseDto response = accountService.selectAccount(loginId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(response);
    }


    /**
     * 현재 회원상태 정보를 조회한다.
     *
     * @param accountId the account id
     * @return the account status
     */
    @GetMapping("/{accountId}/status")
    public ResponseEntity<SelectAccountStatusResponseDto> getAccountStatus(@PathVariable Long accountId) {
        SelectAccountStatusResponseDto response = accountService.selectAccountStatus(accountId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(response);
    }

    /**
     * 회원상태를 변경한다.
     *
     * @param accountId the account id
     * @param code      the code
     * @return the response entity
     */
    @PutMapping("/{accountId}/status")
    public ResponseEntity<UpdateAccountStatusResponseDto> putAccountStatus(@PathVariable Long accountId,
                                                                           @RequestParam String code) {
        String uppercaseCode = code.toUpperCase();
        if (!AccountStatus.Code.matches(uppercaseCode)) {
            throw new AccountStatusNotFoundException(code);
        }

        UpdateAccountStatusResponseDto response = accountService.updateAccountStatus(accountId, uppercaseCode);
        return ResponseEntity.status(HttpStatus.OK)
            .body(response);
    }

    /**
     * 회원이 존재하는지만 조회한다.
     *
     * @param accountId the account id
     * @return the response entity
     */
    @GetMapping("/{accountId}/exists")
    public ResponseEntity<Void> getAccountExists(@PathVariable Long accountId) {
        HttpStatus httpStatus = accountService.selectAccountExists(accountId);
        return ResponseEntity.status(httpStatus)
            .build();
    }

    /**
     * 회원아이디 기준으로 존재하는지 조회한다.
     *
     * @param loginId the login id
     * @return the response entity
     */
    @GetMapping("/login-id-exists/{loginId}")
    public ResponseEntity<Void> getAccountExists(@PathVariable String loginId) {
        HttpStatus httpStatus = accountService.selectAccountExists(loginId);
        return ResponseEntity.status(httpStatus)
            .build();
    }
}
