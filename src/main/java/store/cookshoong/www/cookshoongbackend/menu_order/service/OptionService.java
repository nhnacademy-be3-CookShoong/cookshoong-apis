package store.cookshoong.www.cookshoongbackend.menu_order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.exception.option.OptionGroupNotFoundException;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateOptionRequestDto;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.option.OptionRepository;
import store.cookshoong.www.cookshoongbackend.menu_order.repository.optiongroup.OptionGroupRepository;

/**
 * 관리자가 가맹점의 옵션을 관리.
 * 옵션 추가, 수정, 삭제.
 *
 * @author papel
 * @since 2023.07.11
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OptionService {
    private final OptionRepository optionRepository;
    private final OptionGroupRepository optionGroupRepository;

    /**
     * 사업자 : 옵션 등록 서비스 구현.
     * 옵션 등록.
     *
     * @param createOptionRequestDto 옵션 등록을 위한 정보
     */
    public void createOption(Integer optionGroupId, CreateOptionRequestDto createOptionRequestDto) {
        OptionGroup optionGroup = optionGroupRepository.findById(optionGroupId)
            .orElseThrow(OptionGroupNotFoundException::new);
        Option option =
            new Option(
                optionGroup,
                createOptionRequestDto.getName(),
                createOptionRequestDto.getPrice(),
                0,
                false
                );
        optionRepository.save(option);
    }
}
