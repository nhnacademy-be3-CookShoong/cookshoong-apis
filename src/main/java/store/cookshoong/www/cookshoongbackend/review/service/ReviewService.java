package store.cookshoong.www.cookshoongbackend.review.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtilResolver;
import store.cookshoong.www.cookshoongbackend.file.service.FileUtils;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.exception.OrderNotFoundException;
import store.cookshoong.www.cookshoongbackend.order.repository.OrderRepository;
import store.cookshoong.www.cookshoongbackend.review.entity.Review;
import store.cookshoong.www.cookshoongbackend.review.entity.ReviewHasImage;
import store.cookshoong.www.cookshoongbackend.review.model.ReviewStatusManager;
import store.cookshoong.www.cookshoongbackend.review.model.request.CreateReviewRequestDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewImageResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.review.repository.ReviewRepository;
import store.cookshoong.www.cookshoongbackend.shop.exception.store.UserAccessDeniedException;

/**
 * 리뷰 등록 및 조회.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final OrderRepository orderRepository;
    private final FileUtilResolver fileUtilResolver;
    private final ReviewRepository reviewRepository;
    private final ReviewStatusManager reviewStatusManager;

    /**
     * 사용자가 주문내역을 통해 리뷰를 등록함.
     * 주문완료의 상태가 아니거나, 해당 주문내역의 회원이 아닌 다른 사람이 접근할 경우 접근 권한 없음으로 거부되도록 함.
     * 이미지가 여러개 등록될 수 있으며 이미지가 있을 경우에만 저장시키는 작업을 진행하고, 이미지가 없다면 바로 review를 저장시킴.
     *
     * @param accountId  the account id
     * @param requestDto the request dto
     * @param images     the images
     * @param storedAt   the stored at
     * @throws IOException the io exception
     */
    public void createReview(Long accountId,
                             CreateReviewRequestDto requestDto,
                             List<MultipartFile> images,
                             String storedAt) throws IOException {
        Order order = orderRepository.findById(UUID.fromString(requestDto.getOrderCode()))
            .orElseThrow(OrderNotFoundException::new);

        if (!(order.getAccount().getId().equals(accountId))
            || !reviewStatusManager.isReviewWritableStatus(order.getOrderStatus().getCode())) {
            throw new UserAccessDeniedException("접근 권한이 없습니다.");
        }

        Review review = new Review(order, requestDto);

        if (Objects.nonNull(images)) {
            FileUtils fileUtils = fileUtilResolver.getFileService(storedAt);
            for (MultipartFile file : images) {
                Image image = fileUtils.storeFile(file, FileDomain.REVIEW.getVariable(), true);
                review.getReviewHasImages().add(
                    new ReviewHasImage(new ReviewHasImage.Pk(review.getId(), image.getId()), review, image));
            }
        }
        reviewRepository.save(review);
    }

    /**
     * 사용자 리뷰 목록 페이지로 조회.
     *
     * @param accountId the account id
     * @param pageable  the pageable
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<SelectReviewResponseDto> selectReviewByAccount(Long accountId, Pageable pageable) {
        Page<SelectReviewResponseDto> responses = reviewRepository.lookupReviewByAccount(accountId, pageable);

        for (SelectReviewResponseDto reviewResponse : responses) {
            FileUtils fileUtils = fileUtilResolver.getFileService(reviewResponse.getStoreImageLocationType());
            reviewResponse.setStoreImageName(
                fileUtils.getFullPath(reviewResponse.getStoreImageDomainName(), reviewResponse.getStoreImageName()));

            for (SelectReviewImageResponseDto response : reviewResponse.getImageResponses()) {
                if (Objects.nonNull(response.getLocationType())) {
                    FileUtils reviewFileUtils = fileUtilResolver.getFileService(response.getLocationType());
                    response.setSavedName(
                        reviewFileUtils.getFullPath(response.getDomainName(), response.getSavedName()));
                }
            }
        }
        return responses;
    }

    /**
     * 사장님 매장 리뷰 목록 페이지로 조회.
     *
     * @param storeId   the store id
     * @param pageable  the pageable
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<SelectReviewStoreResponseDto> selectReviewByStore(Long storeId, Pageable pageable) {

        Page<SelectReviewStoreResponseDto> responses = reviewRepository.lookupReviewByStore(storeId, pageable);

        for (SelectReviewStoreResponseDto reviewResponse : responses) {
            FileUtils fileUtils = fileUtilResolver.getFileService(
                reviewResponse.getSelectReviewResponse().getStoreImageLocationType());
            reviewResponse.getSelectReviewResponse().setStoreImageName(fileUtils.getFullPath(
                reviewResponse.getSelectReviewResponse().getStoreImageDomainName(),
                reviewResponse.getSelectReviewResponse().getStoreImageName()));
            for (SelectReviewImageResponseDto dto : reviewResponse.getSelectReviewResponse().getImageResponses()) {
                if (Objects.nonNull(dto.getLocationType())) {
                    FileUtils reviewFileUtils = fileUtilResolver.getFileService(dto.getLocationType());
                    dto.setSavedName(reviewFileUtils.getFullPath(dto.getDomainName(), dto.getSavedName()));
                }
            }
        }
        return responses;
    }
}
