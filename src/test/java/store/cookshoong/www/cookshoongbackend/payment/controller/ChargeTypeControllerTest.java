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
import store.cookshoong.www.cookshoongbackend.payment.service.ChargeTypeService;

/**
 * 결제 타입에 대한 Controller Test.
 *
 * @author jeongjewan
 * @since 2023.07.07
 */
@WebMvcTest(ChargeTypeController.class)
class ChargeTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChargeTypeService chargeTypeService;

    @Test
    @DisplayName("POST 결제 타입 등록")
    void postCreateChargeType() throws Exception {
        CreateTypeRequestDto requestDto = ReflectionUtils.newInstance(CreateTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "토스결제");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/payments/charges")
            .contentType(APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(requestDto.getName()));
    }

    @Test
    @DisplayName("POST 결제 타입 등록 실패: null 값이 들어갈 때 오류 테스트")
    void postCreateChargeTypeNotBlank_1() throws Exception {
        CreateTypeRequestDto requestDto = ReflectionUtils.newInstance(CreateTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", null);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/payments/charges")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST 결제 타입 등록 실패: 빈 값이 들어갈 때 오류 테스트")
    void postCreateChargeTypeNotBlank_2() throws Exception {
        CreateTypeRequestDto requestDto = ReflectionUtils.newInstance(CreateTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/payments/charges")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST 결제 타입 등록 실패: 숫자 값이 들어갈 때 오류 테스트")
    void postCreateChargeTypeNotNumber() throws Exception {
        CreateTypeRequestDto requestDto = ReflectionUtils.newInstance(CreateTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "1234");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/payments/charges")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT 결제 타입 수정")
    void putModifyChargeType() throws Exception {
        ModifyTypeRequestDto requestDto = ReflectionUtils.newInstance(ModifyTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "페이코결제");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        Long chargeTypeId = 1L;

        mockMvc.perform(put("/api/payments/charges/{chargeTypeId}", chargeTypeId)
            .contentType(APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(requestDto.getName()));
    }

    @Test
    @DisplayName("PUT 결제 타입 수정 실패: null 값이 들어갈 때 오류 테스트")
    void putModifyChargeTypeNotBlank_1() throws Exception {
        ModifyTypeRequestDto requestDto = ReflectionUtils.newInstance(ModifyTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", null);

        String requestBody = objectMapper.writeValueAsString(requestDto);

        Long chargeTypeId = 1L;

        mockMvc.perform(put("/api/payments/charges/{chargeTypeId}", chargeTypeId)
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT 결제 타입 수정 살퍄: 빈 값이 들어갈 때 오류 테스트")
    void putModifyChargeTypeNotBlank_2() throws Exception {
        ModifyTypeRequestDto requestDto = ReflectionUtils.newInstance(ModifyTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        Long chargeTypeId = 1L;

        mockMvc.perform(put("/api/payments/charges/{chargeTypeId}", chargeTypeId)
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT 결제 타입 수정 실패: 숫자 값이 들어갈 때 오류 테스트")
    void putModifyChargeTypeNotNumber() throws Exception {
        ModifyTypeRequestDto requestDto = ReflectionUtils.newInstance(ModifyTypeRequestDto.class);
        ReflectionTestUtils.setField(requestDto, "name", "1234");

        String requestBody = objectMapper.writeValueAsString(requestDto);

        Long chargeTypeId = 1L;

        mockMvc.perform(put("/api/payments/charges/{chargeTypeId}", chargeTypeId)
                .contentType(APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET 해당 아이디에 대한 결제 타입 조회")
    void getChargeType() throws Exception {
        Long chargeTypeId = 1L;
        TypeResponseDto responseDto = new TypeResponseDto(1L, "토스결제");

        when(chargeTypeService.selectChargeType(chargeTypeId)).thenReturn(responseDto);

        mockMvc.perform(get("/api/payments/charges/{chargeTypeId}", chargeTypeId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(responseDto.getName()));

        verify(chargeTypeService, times(1)).selectChargeType(chargeTypeId);
    }

    @Test
    @DisplayName("GET 모든 결제애 대한 결제 타입 조회")
    void getChargeTypeAll() throws Exception {
        TypeResponseDto responseDto = new TypeResponseDto(1L, "토스결제");
        List<TypeResponseDto> responseDtoList = Collections.singletonList(responseDto);

        when(chargeTypeService.selectChargeTypeAll()).thenReturn(responseDtoList);

        mockMvc.perform(get("/api/payments/charges"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value(responseDto.getName()));

        verify(chargeTypeService, times(1)).selectChargeTypeAll();
    }

    @Test
    @DisplayName("DELETE 해당 아이디에 대한 결제 타입 삭제")
    void deleteChargeType() throws Exception {
        Long chargeTypeId = 1L;

        mockMvc.perform(delete("/api/payments/charges/{chargeTypeId}", chargeTypeId))
            .andExpect(status().isNoContent());

        verify(chargeTypeService, times(1)).removeChargeType(chargeTypeId);
    }
}
