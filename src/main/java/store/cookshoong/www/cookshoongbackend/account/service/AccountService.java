package store.cookshoong.www.cookshoongbackend.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountsStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.account.exception.DuplicatedUserException;
import store.cookshoong.www.cookshoongbackend.account.model.request.SignUpRequestDto;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.AccountsStatusRepository;
import store.cookshoong.www.cookshoongbackend.account.repository.RankRepository;

/**
 * 회원 가입, 조회, 정보 수정을 다루는 서비스
 *
 * @author koesnam
 * @since 2023.07.05
 */

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    public final AccountRepository accountRepository;
    public final RankRepository rankRepository;
    public final AccountsStatusRepository accountsStatusRepository;

    @Transactional
    public boolean saveAccount(SignUpRequestDto signUpRequestDto, Authority authority) {
        String loginId = signUpRequestDto.getLoginId();
        if (accountRepository.existsByLoginId(loginId)) {
            throw new DuplicatedUserException(loginId);
        }

        AccountsStatus status = accountsStatusRepository.getReferenceById("ACTIVE");
        Rank defaultRank = rankRepository.getReferenceById("FRIEND");
        Account account = new Account(status, authority, defaultRank, signUpRequestDto);

        accountRepository.save(account);
        return true;
    }

}
