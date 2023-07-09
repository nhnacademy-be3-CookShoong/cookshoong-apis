package store.cookshoong.www.cookshoongbackend.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountsStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.account.exception.DuplicatedUserException;
import store.cookshoong.www.cookshoongbackend.account.exception.UserNotFoundException;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.model.response.SelectAccountResponseDto;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountsStatusRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.AuthorityRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.RankRepository;

/**
 * 회원 가입, 조회, 정보 수정을 다루는 서비스.
 *
 * @author koesnam
 * @since 2023.07.05
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accountRepository;
    private final RankRepository rankRepository;
    private final AccountsStatusRepository accountsStatusRepository;
    private final AuthorityRepository authorityRepository;

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

        AccountsStatus status = accountsStatusRepository.getReferenceById(AccountsStatus.Code.ACTIVE.name());
        Rank defaultRank = rankRepository.getReferenceById(Rank.Code.LEVEL_1.name());
        Authority authority = authorityRepository.getReferenceById(authorityCode.name());

        return accountRepository.save(new Account(status, authority, defaultRank, signUpRequestDto))
            .getId();
    }

    public SelectAccountResponseDto selectAccount(Long accountId) {
        return accountRepository.lookupAccount(accountId)
            .orElseThrow(UserNotFoundException::new);
    }
}
