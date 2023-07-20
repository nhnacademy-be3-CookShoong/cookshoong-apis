package store.cookshoong.www.cookshoongbackend.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.cookshoong.www.cookshoongbackend.file.entity.Image;

/**
 * 이미지 파일 jpa repository.
 *
 * @author seungyeon
 * @since 2023.07.18
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
}
