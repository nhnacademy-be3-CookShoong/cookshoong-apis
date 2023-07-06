package store.cookshoong.www.cookshoongbackend.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.store.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.store.model.request.MerchantRegisterRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.MerchantResponseDto;
import store.cookshoong.www.cookshoongbackend.store.repository.MerchantRepository;

/**
 * 관리자가 가맹점 관리.
 * 가맹점 등록, 가맹점 리스트, 가맹점 이름 수정, 가맹점 삭제.
 *
 * @author seungyeon
 * @since 2023.07.06
 */
@Service
@RequiredArgsConstructor
@Transactional
public class MerchantService {
    private final MerchantRepository merchantRepository;

    /**
     * 가맹점 리스트 조회를 위한 서비스 구현.
     *
     * @param pageable 페이지 정보
     * @return 가맹점 리스트
     */
    @Transactional(readOnly = true)
    public Page<MerchantResponseDto> getMerchantList(Pageable pageable) {
        return merchantRepository.lookupMerchantPage(pageable);
    }

    /**
     * 가맹점 등록을 위한 서비스 구현.
     *
     * @param merchantRegisterRequestDto 가맹점 이름 정보
     */
    public void saveMerchant(MerchantRegisterRequestDto merchantRegisterRequestDto) {
        merchantRepository.save(new Merchant(merchantRegisterRequestDto.getMerchantName()));
    }

    /**
     * 가맹점 수정을 위한 서비스 구현.
     *
     * @param merchantId                 가맹점 아이디
     * @param merchantRegisterRequestDto 수정된 가맹점 이름 정보
     */
    public void updateMerchant(Long merchantId, MerchantRegisterRequestDto merchantRegisterRequestDto) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 가맹점입니다."));
        merchant.modifyMerchant(merchantRegisterRequestDto.getMerchantName());
    }

    /**
     * 가맹점 삭제를 위한 서비스 구현.
     *
     * @param merchantId 가맹점 아이디
     */
    public void deleteMerchant(Long merchantId) {
        if (!merchantRepository.existsById(merchantId)) {
            throw new IllegalArgumentException("존재하지 않는 가맹점입니다.");
        }
        merchantRepository.deleteById(merchantId);
    }

}
