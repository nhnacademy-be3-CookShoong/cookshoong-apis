package store.cookshoong.www.cookshoongbackend.eureka.controller;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 서버 상태 변경을 위한 컨트롤러.
 *
 * @author eora21(김주호)
 * @since 2023.07.11
 */
@Profile("prod | prod2")
@RestController
@RequiredArgsConstructor
public class HealthController {
    private final ApplicationInfoManager applicationInfoManager;

    /**
     * 서버 상태 UP 변경.
     *
     * @return the response entity
     */
    @PostMapping("health-check/recover")
    public ResponseEntity<Void> start() {
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        return ResponseEntity.ok()
            .build();
    }

    /**
     * 서버 상태 STOP 변경.
     *
     * @return the response entity
     */
    @PostMapping("health-check/fail")
    public ResponseEntity<Void> stop() {
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.DOWN);
        return ResponseEntity.badRequest()
            .build();
    }
}

