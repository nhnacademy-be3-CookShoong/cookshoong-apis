package store.cookshoong.www.cookshoongbackend.review.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static store.cookshoong.www.cookshoongbackend.account.entity.QAccount.account;
import static store.cookshoong.www.cookshoongbackend.file.entity.QImage.image;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrder.order;
import static store.cookshoong.www.cookshoongbackend.order.entity.QOrderDetail.orderDetail;
import static store.cookshoong.www.cookshoongbackend.review.entity.QReview.review;
import static store.cookshoong.www.cookshoongbackend.review.entity.QReviewHasImage.reviewHasImage;
import static store.cookshoong.www.cookshoongbackend.review.entity.QReviewReply.reviewReply;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.review.entity.Review;
import store.cookshoong.www.cookshoongbackend.review.model.response.QSelectBusinessReviewResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.QSelectReviewImageResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.QSelectReviewOrderMenuResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.QSelectReviewResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.QSelectReviewStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.QStore;


/**
 * 리뷰 조회를 관리하는 Repository.
 *
 * @author seungyeon
 * @since 2023.08.13
 */
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {
    private static final QStore store = new QStore("StoreEntity");

    private static BooleanExpression inStore(Long storeId) {
        return store.id.eq(storeId);
    }

    private static BooleanExpression inAccount(Long accountId) {
        return account.id.eq(accountId);
    }

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<SelectReviewResponseDto> lookupReviewByAccount(Long accountId, Pageable pageable) {
        List<SelectReviewResponseDto> responses = lookupReviews(accountId, pageable);
        Long total = lookupTotal(inAccount(accountId));
        return new PageImpl<>(responses, pageable, total);

    }

    private List<SelectReviewResponseDto> lookupReviews(Long accountId, Pageable pageable) {
        return getReviewInfo(inAccount(accountId), pageable)
            .transform(
                groupBy(review)
                    .list(createReview()));
    }

    @Override
    public Page<SelectReviewStoreResponseDto> lookupReviewByStore(Long storeId, Pageable pageable) {
        List<SelectReviewStoreResponseDto> responses = lookupReviewsStore(storeId, pageable);
        Long total = lookupTotal(inStore(storeId));
        return new PageImpl<>(responses, pageable, total);
    }

    private List<SelectReviewStoreResponseDto> lookupReviewsStore(Long storeId, Pageable pageable) {
        return getReviewInfo(inStore(storeId), pageable)
            .transform(
                groupBy(review)
                    .list(new QSelectReviewStoreResponseDto(account.id, account.nickname, createReview())));
    }

    private JPAQuery<Review> getReviewInfo(BooleanExpression filter, Pageable pageable) {
        return jpaQueryFactory
            .selectFrom(review)
            .innerJoin(review.order, order)

            .innerJoin(orderDetail)
            .on(orderDetail.order.eq(order))

            .innerJoin(order.account, account)

            .innerJoin(store)
            .on(order.store.eq(store))

            .leftJoin(reviewReply)
            .on(reviewReply.review.eq(review))

            .leftJoin(reviewHasImage)
            .on(reviewHasImage.review.eq(review))

            .leftJoin(reviewHasImage.image, image)

            .where(filter)
            .orderBy(review.writtenAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());
    }

    private static QSelectReviewResponseDto createReview() {
        return new QSelectReviewResponseDto(store.id, store.name,
            store.storeImage.savedName, store.storeImage.locationType,
            store.storeImage.domainName, review.id, review.contents, review.rating, review.writtenAt, review.updatedAt,
            list(new QSelectReviewImageResponseDto(
                reviewHasImage.image.savedName, reviewHasImage.image.locationType, reviewHasImage.image.domainName)),
            list(new QSelectReviewOrderMenuResponseDto(orderDetail.menu.id, orderDetail.nowName)),
            list(new QSelectBusinessReviewResponseDto(reviewReply.id, reviewReply.contents, reviewReply.writtenAt)));
    }

    private Long lookupTotal(BooleanExpression filter) {
        return jpaQueryFactory
            .select(review.id.count())
            .from(review)
            .innerJoin(review.order, order)

            .innerJoin(orderDetail)
            .on(orderDetail.order.eq(order))

            .innerJoin(order.account, account)

            .innerJoin(store)
            .on(order.store.eq(store))

            .leftJoin(reviewReply)
            .on(reviewReply.review.eq(review))

            .leftJoin(reviewHasImage)
            .on(reviewHasImage.review.eq(review))

            .leftJoin(reviewHasImage.image, image)

            .where(filter)
            .fetchOne();
    }
}
