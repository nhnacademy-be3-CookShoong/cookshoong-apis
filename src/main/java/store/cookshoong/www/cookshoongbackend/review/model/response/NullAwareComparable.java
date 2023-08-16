package store.cookshoong.www.cookshoongbackend.review.model.response;

/**
 * 내부 값이 null인 경우 스스로 판단하여 추후 제거하기 위한 인터페이스.
 *
 * @author eora21 (김주호)
 * @since 2023.08.15
 */
public interface NullAwareComparable<T> extends Comparable<T> {

    boolean isNull();
}
