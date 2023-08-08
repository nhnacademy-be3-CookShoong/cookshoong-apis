package store.cookshoong.www.cookshoongbackend.file.model;

import lombok.RequiredArgsConstructor;

/**
 * 저장소 코드에 대한 enum.
 *
 * @author seungyeon
 * @since 2023.08.06
 */
@RequiredArgsConstructor
public enum LocationType {
    LOCAL_S("localStorage"),
    OBJECT_S("objectStorage");

    private final String variable;

    public String getVariable() {
        return variable;
    }
}
