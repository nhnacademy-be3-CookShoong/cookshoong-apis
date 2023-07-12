package store.cookshoong.www.cookshoongbackend.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.store.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.store.exception.merchant.DuplicatedMerchantException;
import store.cookshoong.www.cookshoongbackend.store.exception.merchant.MerchantNotFoundException;
import store.cookshoong.www.cookshoongbackend.store.model.request.CreateMerchantRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.request.UpdateMerchantRequestDto;
import store.cookshoong.www.cookshoongbackend.store.model.response.SelectMerchantResponseDto;
import store.cookshoong.www.cookshoongbackend.store.repository.merchant.MerchantRepository;

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
     * 관리자 : 가맹점 리스트 조회를 위한 서비스 구현.
     *
     * @param pageable 페이지 정보
     * @return 가맹점 리스트
     */
    @Transactional(readOnly = true)
    public Page<SelectMerchantResponseDto> selectAllMerchants(Pageable pageable) {
        return merchantRepository.lookupMerchantPage(pageable);
    }

    /**
     * 관리자 : 가맹점 등록을 위한 서비스 구현.
     *
     * @param createMerchantRequestDto 가맹점 이름 정보
     */
    public void createMerchant(CreateMerchantRequestDto createMerchantRequestDto) {
        if (merchantRepository.existsMerchantByName(createMerchantRequestDto.getMerchantName())) {
            throw new DuplicatedMerchantException(createMerchantRequestDto.getMerchantName());
        }
        merchantRepository.save(new Merchant(createMerchantRequestDto.getMerchantName()));
    }

    /**
     * 관리자 : 가맹점 수정을 위한 서비스 구현.
     *
     * @param merchantId               가맹점 아이디
     * @param merchantUpdateRequestDto 수정된 가맹점 이름 정보
     */
    public void updateMerchant(Long merchantId, UpdateMerchantRequestDto merchantUpdateRequestDto) {
        Merchant merchant = merchantRepository.findById(merchantId)
            .orElseThrow(MerchantNotFoundException::new);
        if (merchantRepository.existsMerchantByName(merchantUpdateRequestDto.getMerchantName())) {
            throw new DuplicatedMerchantException(merchantUpdateRequestDto.getMerchantName());
        }
        merchant.modifyMerchant(merchantUpdateRequestDto.getMerchantName());
    }

    /**
     * 관리자 : 가맹점 삭제를 위한 서비스 구현.
     *
     * @param merchantId 가맹점 아이디
     */
    public void removeMerchant(Long merchantId) {
        merchantRepository.deleteById(merchantId);
    }

}
