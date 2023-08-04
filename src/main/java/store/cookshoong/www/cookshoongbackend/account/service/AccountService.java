package store.cookshoong.www.cookshoongbackend.account.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.OauthAccount;
import store.cookshoong.www.cookshoongbackend.account.entity.OauthType;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.account.exception.DuplicatedUserException;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.model.request.OAuth2SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountAuthResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountInfoResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.UpdateAccountStatusResponseDto;
import store.cookshoong.www.cookshoongbackend.account.model.vo.SelectAccountAuthDto;
import store.cookshoong.www.cookshoongbackend.account.model.vo.SelectAccountStatusDto;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountStatusRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.AuthorityRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.OauthAccountRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.OauthTypeRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.RankRepository;

/**
 * 회원 가입, 조회, 정보 수정을 다루는 서비스.
 *
 * @author koesnam (추만석)
 * @since 2023.07.05
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accountRepository;
    private final RankRepository rankRepository;
    private final AccountStatusRepository accountStatusRepository;
    private final AuthorityRepository authorityRepository;
    private final OauthAccountRepository oauthAccountRepository;
    private final OauthTypeRepository oauthTypeRepository;

    /**
     * 올바른 데이터가 들어왔는지 확인하고 회원을 DB에 저장 시킨다.
     *
     * @param signUpRequestDto 회원가입 요청 Dto
     * @param authorityCode    권한코드 ( 일반회원, 사업자회원, 관리자 )
     * @return AccountId 저장하고 생성된 Sequence
     */
    @Transactional
    public Long createAccount(SignUpRequestDto signUpRequestDto, Authority.Code authorityCode) {
        String loginId = signUpRequestDto.getLoginId();
        if (accountRepository.existsByLoginId(loginId)) {
            throw new DuplicatedUserException(loginId);
        }

        AccountStatus status = accountStatusRepository.getReferenceById(AccountStatus.Code.ACTIVE.name());
        Rank defaultRank = rankRepository.getReferenceById(Rank.Code.LEVEL_1.name());
        Authority authority = authorityRepository.getReferenceById(authorityCode.name());

        return accountRepository.save(new Account(status, authority, defaultRank, signUpRequestDto))
            .getId();
    }

    /**
     * 회원이 가지는 모든 정보를 조회한다.
     *
     * @param accountId the account id
     * @return the select account response dto
     */
    public SelectAccountResponseDto selectAccount(Long accountId) {
        return accountRepository.lookupAccount(accountId)
            .orElseThrow(UserNotFoundException::new);
    }

    /**
     * 로그인아이디를 통해 자격증명에 필요한 정보를 가져온다.
     *
     * @param loginId the login id
     * @return 자격증명 응답 정보
     */
    public SelectAccountAuthResponseDto selectAccount(String loginId) {
        SelectAccountAuthDto authDto = accountRepository.findByLoginId(loginId)
            .orElseThrow(UserNotFoundException::new);

        return SelectAccountAuthResponseDto.responseDtoFrom(authDto);
    }

    /**
     * 해당 회원의 현재 상태를 조회한다.
     *
     * @param accountId the login id
     * @return the select account status response dto
     */
    public SelectAccountStatusResponseDto selectAccountStatus(Long accountId) {
        SelectAccountStatusDto statusDto = accountRepository.findAccountStatusById(accountId)
            .orElseThrow();

        return SelectAccountStatusResponseDto.responseDtoFrom(statusDto);
    }

    /**
     * 회원 상태를 주어진 값으로 변경한다.
     *
     * @param accountId  the account id
     * @param statusCode the status code
     * @return the select account status response dto
     */
    @Transactional
    public UpdateAccountStatusResponseDto updateAccountStatus(Long accountId, String statusCode) {
        Account account = accountRepository.findAccountById(accountId)
            .orElseThrow(UserNotFoundException::new);
        AccountStatus accountStatus = accountStatusRepository.getReferenceById(statusCode);
        account.updateStatus(accountStatus);

        return new UpdateAccountStatusResponseDto(accountStatus.getDescription(), LocalDateTime.now());
    }

    /**
     * OAuth 로그인후 필요한 회원 정보를 조회한다.
     *
     * @param provider    the provider
     * @param accountCode the account code
     * @return the select account info response dto
     */
    public SelectAccountInfoResponseDto selectAccountInfoForOAuth(String provider, String accountCode) {
        return accountRepository.lookupAccountInfoForOAuth(provider, accountCode)
            .orElseThrow(UserNotFoundException::new);
    }

    /**
     * 일반 회원과 연동된 OAuth 유저를 생성한다.
     *
     * @param accountId   the account id
     * @param accountCode the account code
     * @param provider    the provider
     */
    @Transactional
    public void createOAuth2Account(Long accountId, String accountCode, String provider) {
        Account account = accountRepository.getReferenceById(accountId);
        OauthType oauthType = oauthTypeRepository.getReferenceByProvider(provider);
        oauthAccountRepository.save(new OauthAccount(account, oauthType, accountCode));
    }
}
