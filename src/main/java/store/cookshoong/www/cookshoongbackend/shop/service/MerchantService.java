package store.cookshoong.www.cookshoongbackend.shop.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.exception.merchant.DuplicatedMerchantException;
import store.cookshoong.www.cookshoongbackend.shop.exception.merchant.MerchantNotFoundException;
import store.cookshoong.www.cookshoongbackend.shop.model.request.CreateMerchantRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.request.UpdateMerchantRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.model.response.SelectAllMerchantsResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.repository.merchant.MerchantRepository;

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

    private void duplicatedMerchantName(String name) {
        if (merchantRepository.existsMerchantByName(name)) {
            throw new DuplicatedMerchantException(name);
        }
    }

    /**
     * 관리자 : 가맹점 리스트 조회를 위한 서비스 구현.
     *
     * @param pageable 페이지 정보
     * @return 가맹점 리스트
     */
    @Transactional(readOnly = true)
    public Page<SelectAllMerchantsResponseDto> selectAllMerchants(Pageable pageable) {
        return merchantRepository.lookupMerchantPage(pageable);
    }

    /**
     * 관리자 : 가맹점 등록을 위한 서비스 구현.
     *
     * @param createMerchantRequestDto 가맹점 이름 정보
     */
    public void createMerchant(CreateMerchantRequestDto createMerchantRequestDto) {
        duplicatedMerchantName(createMerchantRequestDto.getMerchantName());
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
        duplicatedMerchantName(merchantUpdateRequestDto.getMerchantName());
        merchant.modifyMerchant(merchantUpdateRequestDto.getMerchantName());
    }

    /**
     * 관리자 : 가맹점 삭제를 위한 서비스 구현.
     *
     * @param merchantId 가맹점 아이디
     */
    public void removeMerchant(Long merchantId) {
        if (!merchantRepository.existsById(merchantId)) {
            throw new MerchantNotFoundException();
        }
        merchantRepository.deleteById(merchantId);
    }

    /**
     * 사업자 : 가맹점 셀렉트 박스를 위한.
     *
     * @return the list
     */
    public List<SelectAllMerchantsResponseDto> selectAllMerchantsForUser() {
        return merchantRepository.findAllByOrderByNameAsc();
    }

}
