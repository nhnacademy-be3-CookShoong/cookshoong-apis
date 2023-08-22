package store.cookshoong.www.cookshoongbackend.review.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.account.entity.Account;
import store.cookshoong.www.cookshoongbackend.account.entity.AccountStatus;
import store.cookshoong.www.cookshoongbackend.account.entity.Authority;
import store.cookshoong.www.cookshoongbackend.account.entity.Rank;
import store.cookshoong.www.cookshoongbackend.common.config.QueryDslConfig;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.file.model.FileDomain;
import store.cookshoong.www.cookshoongbackend.file.model.LocationType;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.Menu;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menu.MenuStatus;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.MenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.MenuHasMenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.option.Option;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.OptionGroup;
import store.cookshoong.www.cookshoongbackend.order.entity.Order;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderDetail;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;
import store.cookshoong.www.cookshoongbackend.payment.entity.Charge;
import store.cookshoong.www.cookshoongbackend.payment.entity.ChargeType;
import store.cookshoong.www.cookshoongbackend.review.entity.Review;
import store.cookshoong.www.cookshoongbackend.review.entity.ReviewHasImage;
import store.cookshoong.www.cookshoongbackend.review.entity.ReviewReply;
import store.cookshoong.www.cookshoongbackend.review.model.request.CreateReviewRequestDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewResponseDto;
import store.cookshoong.www.cookshoongbackend.review.model.response.SelectReviewStoreResponseDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.BankType;
import store.cookshoong.www.cookshoongbackend.shop.entity.Merchant;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;
import store.cookshoong.www.cookshoongbackend.shop.entity.StoreStatus;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

/**
 * Review Repository TEST.
 *
 * @author jeongjewan
 * @since 2023.08.14
 */
@Slf4j
@DataJpaTest
@Import({QueryDslConfig.class,
    TestEntity.class})
class ReviewRepositoryImplTest {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    TestEntityManager em;
    @Autowired
    TestEntity tm;

    ChargeType chargeType;
    OrderStatus orderStatus;
    Store store;
    Account account;
    Merchant merchant;
    Image businessImage;
    Image storeImage;
    Image menuImage;
    Image reviewImage;
    MenuStatus menuStatus;
    Menu menu;
    MenuGroup menuGroup;
    MenuHasMenuGroup menuHasMenuGroup;
    OptionGroup optionGroup;
    Option option;
    Order order;
    Charge charge;
    String paymentKey = "toss";
    Review review;
    UUID uuid = UUID.randomUUID();
    @BeforeEach
    void setup() {

        AccountStatus accountStatus = tm.getAccountStatusActive();
        Authority authority = new Authority("BUSINESS", "사업자회원");
        Rank rank = tm.getRankLevelOne();

        account = tm.getAccount(accountStatus, authority, rank);
        merchant = tm.getMerchant();

        StoreStatus storeStatus = tm.getStoreStatusOpen();
        BankType bankType = tm.getBankTypeKb();
        businessImage = tm.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.BUSINESS_INFO_IMAGE.getVariable(), "사업자등록증.png", false);
        storeImage = tm.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.STORE_IMAGE.getVariable(), "매장사진.png", true);
        store = tm.getStore(merchant, account, bankType, storeStatus, businessImage, storeImage);

        em.persist(accountStatus);
        em.persist(authority);
        em.persist(rank);
        em.persist(account);
        em.persist(merchant);
        em.persist(storeStatus);
        em.persist(bankType);
        em.persist(businessImage);
        em.persist(storeImage);
        em.persist(store);

        orderStatus = ReflectionUtils.newInstance(OrderStatus.class);
        ReflectionTestUtils.setField(orderStatus, "code", "COMPLETE");
        ReflectionTestUtils.setField(orderStatus, "description", "주문완료");

        menuImage = tm.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.MENU_IMAGE.getVariable(), "메뉴사진.png", true);
        menuStatus = ReflectionUtils.newInstance(MenuStatus.class);
        ReflectionTestUtils.setField(menuStatus, "code", "OPEN");
        ReflectionTestUtils.setField(menuStatus, "description", "판매중");

        menu = new Menu(menuStatus, store, "후라이드 치킨", 19000, "넘버원 바삭바삭", menuImage, 20, BigDecimal.ONE);
        menuGroup = new MenuGroup(store, "후라이드", "바삭바삭", 0);
        menuHasMenuGroup = ReflectionUtils.newInstance(MenuHasMenuGroup.class);
        ReflectionTestUtils.setField(menuHasMenuGroup, "menu", menu);
        ReflectionTestUtils.setField(menuHasMenuGroup, "menuGroup", menuGroup);
        ReflectionTestUtils.setField(menuHasMenuGroup, "menuSequence", 1);
        optionGroup = new OptionGroup(store, "매운맛", null, null, Boolean.FALSE);

        option = new Option(optionGroup, "매운 매문맛", 2000, 0, Boolean.FALSE);
        Option option2 = new Option(optionGroup, "매운 보통", 2000, 0, Boolean.FALSE);
        Option option3 = new Option(optionGroup, "매운 착한", 2000, 0, Boolean.FALSE);

        List<Option> optionList = new ArrayList<>();
        optionList.add(option);
        optionList.add(option2);
        optionList.add(option3);

        em.persist(menuImage);
        em.persist(menuStatus);
        em.persist(menu);
        em.persist(menuGroup);
        em.persist(optionGroup);
        em.persist(optionList.get(0));
        em.persist(optionList.get(1));
        em.persist(optionList.get(2));

        order = ReflectionUtils.newInstance(Order.class);
        ReflectionTestUtils.setField(order, "code", uuid);
        ReflectionTestUtils.setField(order, "orderStatus", orderStatus);
        ReflectionTestUtils.setField(order, "account", account);
        ReflectionTestUtils.setField(order, "store", store);
        ReflectionTestUtils.setField(order, "deliveryAddress", "서남쪽");
        ReflectionTestUtils.setField(order, "deliveryCost", 2000);
        ReflectionTestUtils.setField(order, "orderedAt", LocalDateTime.now());
        ReflectionTestUtils.setField(order, "memo", "맛나게 해주세요");

        chargeType = new ChargeType("toss", "토스결제", false);

        em.persist(orderStatus);
        em.persist(order);

        OrderDetail orderDetail = new OrderDetail(order, menu, 1, menu.getName(), menu.getPrice());
        em.persist(orderDetail);

        em.persist(chargeType);

        charge = new Charge(chargeType, order, LocalDateTime.now(), 54000, paymentKey);
        em.persist(charge);

        CreateReviewRequestDto createReviewRequestDto = ReflectionUtils.newInstance(CreateReviewRequestDto.class);
        ReflectionTestUtils.setField(createReviewRequestDto, "contents", "와우 정말 맛있어요!!");
        ReflectionTestUtils.setField(createReviewRequestDto, "rating", 4);

        review = new Review(order, createReviewRequestDto);

        reviewRepository.save(review);

        reviewImage = tm.getImage(LocationType.OBJECT_S.getVariable(), FileDomain.MENU_IMAGE.getVariable(), "메뉴사진.png", true);

        ReviewHasImage reviewHasImage = new ReviewHasImage(
            new ReviewHasImage.Pk(review.getId(), reviewImage.getId()),
            review, reviewImage
        );
        em.persist(reviewHasImage);

        ReviewReply reviewReply = new ReviewReply(review, "감사합니다.");
        em.persist(reviewReply);
    }

    @Test
    void lookupReviewByAccount() {

        Page<SelectReviewResponseDto> accountReviews =
            reviewRepository.lookupReviewByAccount(account.getId(), Pageable.ofSize(10));

        assertNotNull(accountReviews);
    }

    @Test
    void lookupReviewByStore() {

        Page<SelectReviewStoreResponseDto> accountReviews =
            reviewRepository.lookupReviewByStore(store.getId(), Pageable.ofSize(10));

        assertNotNull(accountReviews);
    }
}
