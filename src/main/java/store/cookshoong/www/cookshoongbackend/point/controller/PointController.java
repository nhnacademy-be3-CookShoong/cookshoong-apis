package store.cookshoong.www.cookshoongbackend.point.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.cookshoong.www.cookshoongbackend.point.model.response.PointLogResponseDto;
import store.cookshoong.www.cookshoongbackend.point.model.response.PointResponseDto;
import store.cookshoong.www.cookshoongbackend.point.service.PointService;

/**
 * 포인트 관련 restController.
 *
 * @author eora21 (김주호)
 * @since 2023.08.08
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point")
public class PointController {
    private final PointService pointService;

    @GetMapping("/{accountId}")
    public ResponseEntity<PointResponseDto> getMyPoint(@PathVariable Long accountId) {
        return ResponseEntity.ok(pointService.selectSumPoint(accountId));
    }

    @GetMapping("/{accountId}/log")
    public ResponseEntity<Page<PointLogResponseDto>> getMyPointLog(@PathVariable Long accountId, Pageable pageable) {
        return ResponseEntity.ok(pointService.selectAllPointLog(accountId, pageable));
    }
}
