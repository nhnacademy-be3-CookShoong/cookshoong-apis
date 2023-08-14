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

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.cookshoong.www.cookshoongbackend.file.entity.QImage;
import store.cookshoong.www.cookshoongbackend.review.entity.QReviewHasImage;
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

    private final JPAQueryFactory jpaQueryFactory;
    QStore store = new QStore("StoreEntity");

    @Override
    public Page<SelectReviewResponseDto> lookupReviewByAccount(Long accountId, Pageable pageable) {
        List<SelectReviewResponseDto> responseDtos = lookupReviews(accountId, pageable);

        Long total = lookupTotal(accountId);
        return new PageImpl<>(responseDtos, pageable, total);
    }

    private List<SelectReviewResponseDto> lookupReviews(Long accountId, Pageable pageable) {

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

            .where(account.id.eq(accountId))
            .orderBy(review.writtenAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .transform(
                groupBy(review)
                    .list(new QSelectReviewResponseDto(store.id, store.name,
                            store.storeImage.savedName, store.storeImage.locationType,
                            store.storeImage.domainName, review.contents, review.rating, review.writtenAt, review.updatedAt,
                            list(new QSelectReviewImageResponseDto(
                                reviewHasImage.image.savedName, reviewHasImage.image.locationType, reviewHasImage.image.domainName)),
                            list(new QSelectReviewOrderMenuResponseDto(orderDetail.menu.id, orderDetail.nowName)),
                            list(new QSelectBusinessReviewResponseDto(reviewReply.contents, reviewReply.writtenAt))
                        )
                    )
            );

    }

    @Override
    public Page<SelectReviewStoreResponseDto> lookupReviewByStore(Long storeId, Pageable pageable) {
        List<SelectReviewStoreResponseDto> responseDtos = lookupReviewsStore(storeId, pageable);

        Long total = lookupTotal(storeId);
        return new PageImpl<>(responseDtos, pageable, total);
    }

    private List<SelectReviewStoreResponseDto> lookupReviewsStore(Long storeId, Pageable pageable) {

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

            .where(account.id.eq(storeId))
            .orderBy(review.writtenAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .transform(
                groupBy(review)
                    .list(new QSelectReviewStoreResponseDto(account.nickname, new QSelectReviewResponseDto(store.id, store.name,
                        store.storeImage.savedName, store.storeImage.locationType,
                        store.storeImage.domainName, review.contents, review.rating, review.writtenAt, review.updatedAt,
                        list(new QSelectReviewImageResponseDto(
                            reviewHasImage.image.savedName, reviewHasImage.image.locationType, reviewHasImage.image.domainName)),
                        list(new QSelectReviewOrderMenuResponseDto(orderDetail.menu.id, orderDetail.nowName)),
                        list(new QSelectBusinessReviewResponseDto(reviewReply.contents, reviewReply.writtenAt))
                    )
                    ))
            );
    }

    private Long lookupTotal(Long accountId) {

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

            .where(account.id.eq(accountId))
            .fetchOne();
    }
}
