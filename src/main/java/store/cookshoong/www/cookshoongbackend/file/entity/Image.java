package store.cookshoong.www.cookshoongbackend.file.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * image 객체.
 *
 * @author seungyeon
 * @since 2023.07.18
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long id;
    @Column(name = "location_code", nullable = false)
    private String locationType;

    @Column(name = "domain_name", nullable = false)
    private String domainName;
    @Column(name = "origin_name", nullable = false)
    private String originName;

    @Column(name = "saved_name", nullable = false, length = 40)
    private String savedName;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    /**
     * 이미지에 대한 메타데이터 : 저장소정보, 폴더, 기존 파일 이름, 저장될 파일이름, 공개여부.
     *
     * @param locationType the location code
     * @param domainName   the domain name
     * @param originName   the origin name
     * @param savedName    the saved name
     * @param isPublic     the is public
     */
    public Image(String locationType, String domainName, String originName, String savedName, boolean isPublic) {
        this.locationType = locationType;
        this.domainName = domainName;
        this.originName = originName;
        this.savedName = savedName;
        this.isPublic = isPublic;
    }
    public void updateImageInfo(String originName){
        this.originName = originName;
    }
}
