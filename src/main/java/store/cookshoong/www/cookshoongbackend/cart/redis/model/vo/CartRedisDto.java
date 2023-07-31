package store.cookshoong.www.cookshoongbackend.cart.redis.model.vo;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 장바구니에 저장될 Dto.
 *
 * @author jeongjewan
 * @since 2023.07.20
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CartRedisDto {
    @NotNull
    private Long accountId;
    @NotNull
    private Long storeId;
    @NotBlank
    private String storeName;
    private CartMenuDto menu;
    private List<CartOptionDto> options;
    private Long createTimeMillis;
    private String hashKey;
    private int count;
    private String menuOptName;
    private String totalMenuPrice;

    /**
     * 메뉴와 옵션을 조합해서 hashKey 를 생성하는 메서드.
     *
     * @return      메뉴 + 옵션s -> hashKey 반환
     */
    public String generateUniqueHashKey() {
        String optionIdsString = options.stream()
            .map(cartOptionDto -> cartOptionDto.getOptionId().toString())
            .collect(Collectors.joining(""));

        return menu.getMenuId() + optionIdsString;
    }

    /**
     * 메뉴와 옵션에 이름을 조합하는 메서드.          <br>
     *
     *
     * @return      후라이드치킨 + 양념소스 -> hashKey 반환
     */
    public String generateMenuOptionName() {
        String optionNamesString = options.stream()
            .map(CartOptionDto::getOptionName)
            .collect(Collectors.joining(","));

        return menu.getMenuName() + "<br>옵션: " + optionNamesString;
    }

    /**
     * 메뉴와 옵션에 가격에 총합을 계산하는 메서드.
     *
     * @return      "총합: 가격"
     */
    public String generateTotalMenuPrice() {
        int menuPrice = menu.getMenuPrice();
        int optionPrices = options.stream()
            .mapToInt(CartOptionDto::getOptionPrice)
            .sum();

        return String.valueOf((menuPrice + optionPrices) * count);
    }

    /**
     * 장바구니가 만들어진 시간을 생성 -> 장바구니에 넣은 순서대로 정렬해주기 위해 사용.
     */
    public void createTimeMillis() {
        this.createTimeMillis = System.currentTimeMillis();
    }

    /**
     * Dto 안에 hashKey 를 담아두기 위해 BacK API 에서 generateUniqueHashKey 메서드를 사용해서 넣어줌.
     *
     * @param hashKey       redis hashKey
     */
    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    /**
     * Front 에서 보여줄 메뉴 + 옵션 이름을 조합해서 menuOptName 값 주입.
     *
     * @param menuOptName       메뉴와 옵션 이름 병합
     */
    public void setMenuOptName(String menuOptName) {
        this.menuOptName = menuOptName;
    }

    /**
     * 메뉴와 옵션에 대한 가격 총합.
     *
     * @param totalMenuPrice        가격에 대한 총합을 반환.
     */
    public void setTotalMenuPrice(String totalMenuPrice) {
        this.totalMenuPrice = totalMenuPrice;
    }

    /**
     * 수량이 100보다 작으면 증가, 100 초과이면 100 유지.
     *
     */
    public void incrementCount() {

        if (count <= 100) {
            count++;
        }
    }

    /**
     * 수량이 1보다 크면 감소, 1 미만이면 유지.
     */
    public void decrementCount() {

        if (count > 1) {
            count--;
        }
    }
}
