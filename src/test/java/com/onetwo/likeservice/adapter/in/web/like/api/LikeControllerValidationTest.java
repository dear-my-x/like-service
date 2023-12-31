package com.onetwo.likeservice.adapter.in.web.like.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onetwo.likeservice.adapter.in.web.config.TestConfig;
import com.onetwo.likeservice.adapter.in.web.like.mapper.LikeDtoMapper;
import com.onetwo.likeservice.adapter.in.web.like.request.RegisterLikeRequest;
import com.onetwo.likeservice.application.port.in.usecase.RegisterLikeUseCase;
import com.onetwo.likeservice.common.GlobalUrl;
import com.onetwo.likeservice.common.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
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
class LikeControllerValidationTest {

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

    @ParameterizedTest
    @NullSource
    @WithMockUser(username = userId)
    @DisplayName("[단위][Web Adapter] Like 등록 category validation fail - 실패 테스트")
    void registerLikeCategoryValidationFailTest(Integer testCategory) throws Exception {
        //given
        RegisterLikeRequest registerLikeRequest = new RegisterLikeRequest(testCategory, targetId);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(GlobalUrl.LIKE_ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerLikeRequest))
                        .accept(MediaType.APPLICATION_JSON));
        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @ParameterizedTest
    @NullSource
    @WithMockUser(username = userId)
    @DisplayName("[단위][Web Adapter] Like 등록 target id validation fail - 실패 테스트")
    void registerLikeTargetIdValidationFailTest(Long testTargetId) throws Exception {
        //given
        RegisterLikeRequest registerLikeRequest = new RegisterLikeRequest(category, testTargetId);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(GlobalUrl.LIKE_ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerLikeRequest))
                        .accept(MediaType.APPLICATION_JSON));
        //then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print());
    }
}