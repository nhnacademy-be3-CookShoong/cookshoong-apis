package store.cookshoong.www.cookshoongbackend.menu_order.entity.menu;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.menugroup.MenuHasMenuGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.entity.optiongroup.MenuHasOptionGroup;
import store.cookshoong.www.cookshoongbackend.menu_order.model.request.CreateMenuRequestDto;
import store.cookshoong.www.cookshoongbackend.shop.entity.Store;

/**
 * 메뉴 엔티티.
 *
 * @author papel (윤동현)
 * @since 2023.07.11
 */
@Getter
@Entity
@Table(name = "menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_status_code", nullable = false)
    private MenuStatus menuStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Lob
    @Column(name = "description")
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_image_id")
    private Image image;

    @Column(name = "cooking_time", nullable = false)
    private Integer cookingTime;

    @Column(name = "earning_rate", precision = 2, scale = 1)
    private BigDecimal earningRate;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.PERSIST)
    private final Set<MenuHasMenuGroup> menuHasMenuGroups = new HashSet<>();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.PERSIST)
    private final Set<MenuHasOptionGroup> menuHasOptionGroups = new HashSet<>();

    /**
     * 메뉴 엔티티 생성자.
     *
     * @param menuStatus  가맹점
     * @param store       회원
     * @param name        은행타입
     * @param price       가게 상태
     * @param description 사업자등록증
     * @param image       사업자등록번호
     * @param cookingTime 대표자 이름
     * @param earningRate 개업일자
     */
    public Menu(MenuStatus menuStatus, Store store, String name, Integer price, String description, Image image, Integer cookingTime, BigDecimal earningRate) {
        this.menuStatus = menuStatus;
        this.store = store;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.cookingTime = cookingTime;
        this.earningRate = earningRate;
    }

    /**
     * 메뉴의 상태코드 변경.
     *
     * @param menuStatus 메뉴 상태 코드(OPEN, CLOSE, OUTED)
     */
    public void modifyMenuStatus(MenuStatus menuStatus) {
        this.menuStatus = menuStatus;
    }

    /**
     * 메뉴 내용 변경.
     *
     * @param createMenuRequestDto 매뉴 내용 변경 Dto
     */
    public void modifyMenu(CreateMenuRequestDto createMenuRequestDto) {
        this.name = createMenuRequestDto.getName();
        this.description = createMenuRequestDto.getDescription();
        this.price = createMenuRequestDto.getPrice();
        this.cookingTime = createMenuRequestDto.getCookingTime();
        this.earningRate = createMenuRequestDto.getEarningRate();
    }

    /**
     * 메뉴 이미지 수정.
     *
     * @param image the image
     */
    public void modifyImage(Image image) {
        this.image = image;
    }
}
