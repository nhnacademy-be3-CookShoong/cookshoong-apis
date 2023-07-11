package store.cookshoong.www.cookshoongbackend.payment.controller;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import store.cookshoong.www.cookshoongbackend.payment.model.request.CreateTypeRequestDto;
import store.cookshoong.www.cookshoongbackend.payment.model.request.ModifyTypeRequestDto;
import store.cookshoong.www.cookshoongbackend.payment.model.response.TypeResponseDto;
import store.cookshoong.www.cookshoongbackend.payment.service.RefundTypeService;

/**
 * 환불 타입에 대한 Controller Test.
 *
 * @author jeongjewan
 * @since 2023.07.07
 */
@WebMvcTest(RefundTypeController.class)
class RefundTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RefundTypeService refundTypeService;

    @Test
    @DisplayName("POST 환불 타입 등록")
    void postCreateRefundType() throws Exception {
        CreateTypeRequestDto requestDto = ReflectionUtils.newInstance(CreateTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "id", "INPERSON");
        ReflectionTestUtils.setField(requestDto, "name", "개인 변심으로 인한 환불");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/payments/refunds")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(requestDto.getId()))
            .andExpect(jsonPath("$.name").value(requestDto.getName()));
    }

    @Test
    @DisplayName("POST 환불 타입 등록 실패: null 값이 들어갈 때 오류 테스트")
    void postCreateRefundTypeNotBlank_1() throws Exception {
        CreateTypeRequestDto requestDto = ReflectionUtils.newInstance(CreateTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", null);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/payments/refunds")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST 결제 타입 등록 실패: 빈 값이 들어갈 때 오류 테스트")
    void postCreateRefundTypeNotBlank_2() throws Exception {
        CreateTypeRequestDto requestDto = ReflectionUtils.newInstance(CreateTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/payments/refunds")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST 결제 타입 등록 실패: 숫자 값이 들어갈 때 오류 테스트")
    void postCreateRefundTypeNotNumber() throws Exception {
        CreateTypeRequestDto requestDto = ReflectionUtils.newInstance(CreateTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "1234");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/payments/refunds")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT 결제 타입 수정")
    void putModifyRefundType() throws Exception {
        ModifyTypeRequestDto requestDto = ReflectionUtils.newInstance(ModifyTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "전체 환불 요청");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        Long refundTypeId = 1L;

        mockMvc.perform(put("/api/payments/refunds/{refundTypeId}", refundTypeId)
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(requestDto.getName()));
    }

    @Test
    @DisplayName("PUT 환불 타입 수정 실패: null 값이 들어갈 때 오류 테스트")
    void putModifyRefundTypeNotBlank_1() throws Exception {
        ModifyTypeRequestDto requestDto = ReflectionUtils.newInstance(ModifyTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", null);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        Long refundTypeId = 1L;

        mockMvc.perform(put("/api/payments/refunds/{refundTypeId}", refundTypeId)
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT 환불 타입 수정 살퍄: 빈 값이 들어갈 때 오류 테스트")
    void putModifyRefundTypeNotBlank_2() throws Exception {
        ModifyTypeRequestDto requestDto = ReflectionUtils.newInstance(ModifyTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        Long refundTypeId = 1L;

        mockMvc.perform(put("/api/payments/refunds/{refundTypeId}", refundTypeId)
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT 환불 타입 수정 실패: 숫자 값이 들어갈 때 오류 테스트")
    void putModifyRefundTypeNotNumber() throws Exception {
        ModifyTypeRequestDto requestDto = ReflectionUtils.newInstance(ModifyTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "1234");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        Long refundTypeId = 1L;

        mockMvc.perform(put("/api/payments/refunds/{refundTypeId}", refundTypeId)
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET 해당 아이디에 대한 환불 타입 조회")
    void getRefundType() throws Exception {
        String refundTypeId = "INPERSON";
        TypeResponseDto responseDto = new TypeResponseDto("INPERSON", "개인 변심으로 인한 환불");

        when(refundTypeService.selectRefundType(refundTypeId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/payments/refunds/{refundTypeId}", refundTypeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(responseDto.getName()));

        verify(refundTypeService, times(1)).selectRefundType(refundTypeId);
    }

    @Test
    @DisplayName("GET 모든 결제애 대한 환불 타입 조회")
    void getRefundTypeAll() throws Exception {
        TypeResponseDto responseDto = new TypeResponseDto("INPERSON", "개인 변심으로 인한 환불");
        List<TypeResponseDto> responseDtoList = Collections.singletonList(responseDto);

        when(refundTypeService.selectRefundTypeAll()).thenReturn(responseDtoList);

        mockMvc.perform(get("/api/payments/refunds"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value(responseDto.getName()));

        verify(refundTypeService, times(1)).selectRefundTypeAll();
    }

    @Test
    @DisplayName("DELETE 해당 아이디에 대한 환불 타입 삭제")
    void deleteRefundType() throws Exception {
        String refundTypeId = "INPERSON";

        mockMvc.perform(delete("/api/payments/refunds/{refundTypeId}", refundTypeId))
            .andExpect(status().isNoContent());

        verify(refundTypeService, times(1)).removeRefundType(refundTypeId);
    }
}
