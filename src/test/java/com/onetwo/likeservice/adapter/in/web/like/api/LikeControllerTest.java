package com.onetwo.likeservice.adapter.in.web.like.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onetwo.likeservice.adapter.in.web.config.TestConfig;
import com.onetwo.likeservice.adapter.in.web.like.mapper.LikeDtoMapper;
import com.onetwo.likeservice.adapter.in.web.like.request.RegisterLikeRequest;
import com.onetwo.likeservice.adapter.in.web.like.response.RegisterLikeResponse;
import com.onetwo.likeservice.application.port.in.command.RegisterLikeCommand;
import com.onetwo.likeservice.application.port.in.response.RegisterLikeResponseDto;
import com.onetwo.likeservice.application.port.in.usecase.RegisterLikeUseCase;
import com.onetwo.likeservice.common.GlobalUrl;
import com.onetwo.likeservice.common.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LikeController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                        SecurityConfig.class
                })
        })
@Import(TestConfig.class)
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegisterLikeUseCase registerLikeUseCase;

    @MockBean
    private LikeDtoMapper likeDtoMapper;

    private final String userId = "testUserId";
    private final int category = 1;
    private final long targetId = 11L;

    @Test
    @WithMockUser(username = userId)
    @DisplayName("[단위][Web Adapter] Like 등록 - 성공 테스트")
    void registerLikeSuccessTest() throws Exception {
        //given
        RegisterLikeRequest registerLikeRequest = new RegisterLikeRequest(category, targetId);
        RegisterLikeCommand registerLikeCommand = new RegisterLikeCommand(userId, registerLikeRequest.category(), registerLikeRequest.targetId());
        RegisterLikeResponseDto registerLikeResponseDto = new RegisterLikeResponseDto(true);
        RegisterLikeResponse registerLikeResponse = new RegisterLikeResponse(true);

        when(likeDtoMapper.registerRequestToCommand(anyString(), any(RegisterLikeRequest.class))).thenReturn(registerLikeCommand);
        when(registerLikeUseCase.registerLike(any(RegisterLikeCommand.class))).thenReturn(registerLikeResponseDto);
        when(likeDtoMapper.dtoToRegisterResponse(any(RegisterLikeResponseDto.class))).thenReturn(registerLikeResponse);
        //when
        ResultActions resultActions = mockMvc.perform(
                post(GlobalUrl.LIKE_ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerLikeRequest))
                        .accept(MediaType.APPLICATION_JSON));
        //then
        resultActions.andExpect(status().isCreated())
                .andDo(print());
    }
}