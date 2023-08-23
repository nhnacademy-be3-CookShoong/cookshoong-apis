package store.cookshoong.www.cookshoongbackend.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import store.cookshoong.www.cookshoongbackend.order.entity.OrderStatus;
import store.cookshoong.www.cookshoongbackend.util.TestEntity;

@DataJpaTest
@Import(TestEntity.class)
class OrderStatusRepositoryTest {
    @MockBean
    JPAQueryFactory jpaQueryFactory;

    @Autowired
    OrderStatusRepository orderStatusRepository;

    @Autowired
    TestEntity te;

    @Autowired
    TestEntityManager em;

    OrderStatus orderStatus;

    @BeforeEach
    void beforeEach() {
        orderStatus = te.createUsingDeclared(OrderStatus.class);
        ReflectionTestUtils.setField(orderStatus, "code", OrderStatus.StatusCode.CANCEL.name());
        ReflectionTestUtils.setField(orderStatus, "description", "테스트용입니다.");
        em.persist(orderStatus);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("주문상태코드로 entity 찾기")
    void findByOrderStatusCodeTest() throws Exception {
        Optional<OrderStatus> byOrderStatusCode =
            orderStatusRepository.findByOrderStatusCode(OrderStatus.StatusCode.CANCEL);
        assertThat(byOrderStatusCode).isNotEmpty();

        OrderStatus getOrderStatus = byOrderStatusCode.get();
        assertThat(getOrderStatus.getCode()).isEqualTo(orderStatus.getCode());
    }
}
