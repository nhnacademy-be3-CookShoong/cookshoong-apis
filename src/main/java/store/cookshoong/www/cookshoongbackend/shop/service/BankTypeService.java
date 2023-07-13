package store.cookshoong.www.cookshoongbackend.shop.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.exception.banktype.DuplicatedBankException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateBankRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllBanksForUserResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllBanksResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.bank.BankTypeRepository;

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

    /**
     * 은행명 추가.
     *
     * @param requestDto 은행 이름 request dto
     */
    public void createBank(CreateBankRequestDto requestDto) {
        if (bankTypeRepository.existsById(requestDto.getBankCode())) {
            throw new DuplicatedBankException(requestDto.getBankCode());
        }
        if (bankTypeRepository.existsByDescription(requestDto.getBankName())) {
            throw new DuplicatedBankException(requestDto.getBankName());
        }
        bankTypeRepository.save(new BankType(requestDto.getBankCode(), requestDto.getBankName()));
    }

    @Transactional(readOnly = true)
    public List<SelectAllBanksForUserResponseDto> selectBanksForUser() {
        return bankTypeRepository.lookupBanks();
    }

}
