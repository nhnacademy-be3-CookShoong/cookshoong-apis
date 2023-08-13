package store.cookshoong.www.cookshoongbackend.review.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static store.cookshoong.www.cookshoongbackend.account.entity.QAccount.account;
import static store.cookshoong.www.cookshoongbackend.file.entity.QImage.image;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrder.order;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrderDetail.orderDetail;
import static store.cookshoong.www.cookshoongbackend.review.entity.QReview.review;
import static store.cookshoong.www.cookshoongbackend.review.entity.QReviewReply.reviewReply;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.account.entity.QAccount;
import store.cookshoong.www.cookshoongbackend.file.entity.QImage;
import store.cookshoong.www.cookshoongbackend.order.entity.QOrder;
import store.cookshoong.www.cookshoongbackend.order.entity.QOrderDetail;
import store.cookshoong.www.cookshoongbackend.review.entity.QReview;
import store.cookshoong.www.cookshoongbackend.review.entity.QReviewHasImage;
import store.cookshoong.www.cookshoongbackend.review.entity.QReviewReply;
import store.cookshoong.www.cookshoongbackend.review.model.response.QSelectBusinessReviewResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.QSelectReviewImageResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.QSelectReviewOrderMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.QSelectReviewResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewImageResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;


/**
 * 리뷰의 사진, 메뉴, 사장님 답글을 한번에 들고오는 메서드.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<SelectReviewResponseDto> lookupReviewByAccount(Long accountId, Pageable pageable) {
        List<SelectReviewResponseDto> responseDtos = lookupReviews(accountId, pageable);

        Long total = lookupTotal(accountId);
        return new PageImpl<>(responseDtos, pageable, total);
    }

    private List<SelectReviewResponseDto> lookupReviews(Long accountId, Pageable pageable) {
        QStore store = QStore.store;
        QReviewHasImage reviewHasImage = QReviewHasImage.reviewHasImage;
        QImage image = QImage.image;

        return jpaQueryFactory
            .selectFrom(review)
            .innerJoin(review.orderCode, order)

            .innerJoin(orderDetail)
            .on(orderDetail.order.eq(order))

            .innerJoin(order)
            .on(order.account.eq(account))

            .innerJoin(order)
            .on(order.store.eq(store))

            .leftJoin(reviewReply.review, review)
            .leftJoin(review.reviewHasImages, reviewHasImage)
            .leftJoin(reviewHasImage.image, image)
            .where(account.id.eq(accountId))
            .orderBy(review.writtenAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .transform(
                groupBy(review)
                    .list(new QSelectReviewResponseDto(review.orderCode.store.id, review.orderCode.store.name,
                            review.orderCode.store.storeImage.savedName, review.orderCode.store.storeImage.locationType,
                            review.orderCode.store.storeImage.domainName, review.contents, review.rating, review.writtenAt, review.updatedAt,
                        list(new QSelectReviewImageResponseDto(
                            reviewHasImage.image.savedName, reviewHasImage.image.locationType, reviewHasImage.image.domainName)),
                        list(new QSelectReviewOrderMenuResponseDto(orderDetail.menu.id, orderDetail.nowName)),
                            list(new QSelectBusinessReviewResponseDto(reviewReply.contents, reviewReply.writtenAt))
                        )
                    )
            );

    }

    private Long lookupTotal(Long accountId) {
        QReview review = QReview.review;
        QReviewHasImage reviewHasImage = QReviewHasImage.reviewHasImage;
        QImage image = QImage.image;
        QOrder order = QOrder.order;
        QStore store = QStore.store;
        QOrderDetail orderDetail = QOrderDetail.orderDetail;
        QReviewReply reviewReply = QReviewReply.reviewReply;
        QAccount account = QAccount.account;

        return jpaQueryFactory
            .select(review.count())
            .from(review)
            .innerJoin(review.orderCode, order)
            .innerJoin(orderDetail)
            .on(orderDetail.order.eq(order))
            .innerJoin(order)
            .on(order.account.eq(account))
            .innerJoin(order)
            .on(order.store.eq(store))
            .leftJoin(review.reviewHasImages, reviewHasImage)
            .leftJoin(reviewHasImage)
            .on(reviewHasImage.image.eq(image))
            .leftJoin(review)
            .on(reviewReply.review.eq(review))
            .where(account.id.eq(accountId))
            .fetchOne();
    }
}
