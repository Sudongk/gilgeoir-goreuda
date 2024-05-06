package com.pd.gilgeorigoreuda.review.controller;

import com.pd.gilgeorigoreuda.login.domain.MemberAccessRefreshToken;
import com.pd.gilgeorigoreuda.login.domain.MemberToken;
import com.pd.gilgeorigoreuda.review.dto.request.ReviewCreateRequest;
import com.pd.gilgeorigoreuda.review.dto.request.ReviewUpdateRequest;
import com.pd.gilgeorigoreuda.review.service.ReviewService;
import com.pd.gilgeorigoreuda.settings.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static com.pd.gilgeorigoreuda.settings.restdocs.RestDocsConfiguration.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureRestDocs
class ReviewControllerTest extends ControllerTest {

    private static final MemberAccessRefreshToken MEMBER_ACCESS_REFRESH_TOKEN = MemberAccessRefreshToken.of("accessToken", "refreshToken");
    private static final MemberToken MEMBER_TOKEN = MemberToken.of(1L, "accessToken", "refreshToken");

    @MockBean
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        given(memberTokenRepository.findByAccessToken(any())).willReturn(Optional.of(MEMBER_TOKEN));
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performCreateReviewRequest(final ReviewCreateRequest reviewCreateRequest, final Long storeId) throws Exception {
        return mockMvc.perform(
                post("/api/v1/reviews/stores/{storeId}", storeId)
                        .header(AUTHORIZATION, MEMBER_ACCESS_REFRESH_TOKEN.getAccessToken())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequest))
        );
    }

    private ResultActions performGetReviewsRequest(final Long storeId) throws Exception {
        return mockMvc.perform(
                get("/api/v1/reviews/stores/{storeId}", storeId)
                        .param("page", "0")
                        .param("size", "10")
        );
    }

    private ResultActions performUpdateReviewRequest(final ReviewUpdateRequest reviewUpdateRequest, final Long reviewId) throws Exception {
        return mockMvc.perform(
                put("/api/v1/reviews/{reviewId}", reviewId)
                        .header(AUTHORIZATION, MEMBER_ACCESS_REFRESH_TOKEN.getAccessToken())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewUpdateRequest))
        );
    }

    private ResultActions performDeleteReviewRequest(final Long reviewId) throws Exception {
        return mockMvc.perform(
                delete("/api/v1/reviews/{reviewId}", reviewId)
                        .header(AUTHORIZATION, MEMBER_ACCESS_REFRESH_TOKEN.getAccessToken())
        );
    }

    private ResultActions performCreateReviewCommentRequest(final ReviewCreateRequest reviewCreateRequest, final Long reviewId) throws Exception {
        return mockMvc.perform(
                post("/api/v1/reviews/{reviewId}/comments", reviewId)
                        .header(AUTHORIZATION, MEMBER_ACCESS_REFRESH_TOKEN.getAccessToken())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequest))
        );
    }

    private ResultActions performGetReviewCommentsRequest(final Long reviewId) throws Exception {
        return mockMvc.perform(
                get("/api/v1/reviews/{reviewId}/comments", reviewId)
                        .header(AUTHORIZATION, MEMBER_ACCESS_REFRESH_TOKEN.getAccessToken())
        );
    }

    private ResultActions performUpdateReviewCommentRequest(final ReviewUpdateRequest reviewUpdateRequest, final Long reviewId, final Long commentId) throws Exception {
        return mockMvc.perform(
                put("/api/v1/reviews/{reviewId}/comments/{commentId}", reviewId, commentId)
                        .header(AUTHORIZATION, MEMBER_ACCESS_REFRESH_TOKEN.getAccessToken())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewUpdateRequest))
        );
    }

    private ResultActions performDeleteReviewCommentRequest(final Long reviewId, final Long commentId) throws Exception {
        return mockMvc.perform(
                delete("/api/v1/reviews/{reviewId}/comments/{commentId}", reviewId, commentId)
                        .header(AUTHORIZATION, MEMBER_ACCESS_REFRESH_TOKEN.getAccessToken())
        );
    }

}