package com.onetwo.likeservice.adapter.in.web.like.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onetwo.likeservice.adapter.in.web.config.TestHeader;
import com.onetwo.likeservice.adapter.in.web.like.request.RegisterLikeRequest;
import com.onetwo.likeservice.common.GlobalStatus;
import com.onetwo.likeservice.common.GlobalUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(TestHeader.class)
class LikeControllerBootTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestHeader testHeader;

    private final String userId = "testUserId";
    private final int category = 1;
    private final long targetId = 11L;

    @Test
    @Transactional
    @DisplayName("[통합][Web Adapter] Like 등록 - 성공 테스트")
    void registerLikeSuccessTest() throws Exception {
        //given
        RegisterLikeRequest registerLikeRequest = new RegisterLikeRequest(category, targetId);

        //when
        ResultActions resultActions = mockMvc.perform(
                post(GlobalUrl.LIKE_ROOT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerLikeRequest))
                        .headers(testHeader.getRequestHeaderWithMockAccessKey(userId))
                        .accept(MediaType.APPLICATION_JSON));
        //then
        resultActions.andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("register-like",
                                requestHeaders(
                                        headerWithName(GlobalStatus.ACCESS_ID).description("서버 Access id"),
                                        headerWithName(GlobalStatus.ACCESS_KEY).description("서버 Access key"),
                                        headerWithName(GlobalStatus.ACCESS_TOKEN).description("유저의 access-token")
                                ),
                                requestFields(
                                        fieldWithPath("category").type(JsonFieldType.NUMBER).description("like를 등록할 target의 category ( 1: posting, 2: comment"),
                                        fieldWithPath("targetId").type(JsonFieldType.NUMBER).description("like를 등록할 target id")
                                ),
                                responseFields(
                                        fieldWithPath("isRegisterSuccess").type(JsonFieldType.BOOLEAN).description("등록 완료 여부")
                                )
                        )
                );
    }
}