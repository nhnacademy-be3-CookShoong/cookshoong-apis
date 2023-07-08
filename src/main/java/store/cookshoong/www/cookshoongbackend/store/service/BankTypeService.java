package store.cookshoong.www.cookshoongbackend.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectAllBanksResponseDto;
import store.cookshoong.www.cookshoongbackend.store.repository.bank.BankTypeRepository;

/**
 * bankType 서비스 구현.
 *
 * @author seungyeon
 * @since 2023.07.08
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BankTypeService {
    private final BankTypeRepository bankTypeRepository;

    /**
     * 은행 리스트 페이지로 구현.
     *
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<SelectAllBanksResponseDto> selectBanks(Pageable pageable) {
        return bankTypeRepository.lookupBanksPage(pageable);
    }
}
