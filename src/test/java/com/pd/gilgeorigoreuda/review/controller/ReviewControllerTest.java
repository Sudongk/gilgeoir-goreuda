package com.pd.gilgeorigoreuda.review.controller;

import com.pd.gilgeorigoreuda.common.page.PageInfo;
import com.pd.gilgeorigoreuda.login.domain.MemberAccessRefreshToken;
import com.pd.gilgeorigoreuda.login.domain.MemberToken;
import com.pd.gilgeorigoreuda.member.domain.entity.Member;
import com.pd.gilgeorigoreuda.review.domain.entity.Review;
import com.pd.gilgeorigoreuda.review.domain.entity.ReviewImage;
import com.pd.gilgeorigoreuda.review.dto.request.ReviewCreateRequest;
import com.pd.gilgeorigoreuda.review.dto.request.ReviewUpdateRequest;
import com.pd.gilgeorigoreuda.review.dto.response.ReviewCreateResponse;
import com.pd.gilgeorigoreuda.review.dto.response.ReviewListResponse;
import com.pd.gilgeorigoreuda.review.dto.response.ReviewMemberResponse;
import com.pd.gilgeorigoreuda.review.dto.response.ReviewResponse;
import com.pd.gilgeorigoreuda.review.service.ReviewCommentService;
import com.pd.gilgeorigoreuda.review.service.ReviewService;
import com.pd.gilgeorigoreuda.settings.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
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

    private static final Long MEMBER_ID = 1L;
    private static final Long STORE_ID = 1L;
    private static final Long REVIEW_ID = 1L;
    private static final Long REVIEW_COMMENT_ID = 1L;


    @MockBean
    private ReviewService reviewService;

    @MockBean
    private ReviewCommentService reviewCommentService;

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

    private ReviewListResponse reviewListResponse() {
        ReviewResponse reviewResponse1 = new ReviewResponse(
                1L,
                "content",
                5,
                0,
                0,
                new ReviewMemberResponse(1L, "nickname1", "profileImageUrl1"),
                STORE_ID,
                List.of("https://image1.com", "https://image2.com"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        ReviewResponse reviewResponse2 = new ReviewResponse(
                2L,
                "content",
                5,
                0,
                0,
                new ReviewMemberResponse(2L, "nickname2", "profileImageUrl2"),
                STORE_ID,
                List.of("https://image1.com", "https://image2.com", "https://image3.com"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        PageInfo pageInfo = new PageInfo(2, 0, 10, 2);

        return new ReviewListResponse(List.of(reviewResponse1, reviewResponse2), pageInfo);
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReviewSuccess() throws Exception {
        // given
        ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                "content",
                5,
                List.of("https://image1.com", "https://image2.com", "https://image3.com")
        );

        ReviewCreateResponse reviewCreateResponse = ReviewCreateResponse.of(REVIEW_ID);

        given(reviewService.createReview(any(), any(), any())).willReturn(reviewCreateResponse);

        // when
        ResultActions resultActions = performCreateReviewRequest(reviewCreateRequest, STORE_ID);

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, "/api/v1/reviews/stores/" + STORE_ID + "/reviews/" + REVIEW_ID))
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("storeId").description("상점 ID")
                                ),
                                requestFields(
                                        fieldWithPath("content")
                                                .description("리뷰 내용")
                                                .attributes(field("constraints", "NotBlank")),
                                        fieldWithPath("reviewRating")
                                                .description("리뷰 평점")
                                                .attributes(field("constraints", "NotNull, Min(1), Max(5)")),
                                        fieldWithPath("imageUrls")
                                                .description("리뷰 이미지 URL 리스트")
                                                .attributes(field("constraints", "Size(5)"))
                                ),
                                responseHeaders(
                                        headerWithName(LOCATION)
                                                .description("생성된 리뷰 URI")
                                )
                        )
                );
    }

    @Test
    @DisplayName("리뷰 목록 조회 성공")
    void getReviewsSuccess() throws Exception {
        // given
        ReviewListResponse reviewListResponse = reviewListResponse();

        given(reviewService.findReviewsByStoreId(any(), any())).willReturn(reviewListResponse);

        // when
        ResultActions resultActions = performGetReviewsRequest(STORE_ID);

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호"),
                                        parameterWithName("size").description("페이지 크기")
                                ),
                                responseFields(
                                        fieldWithPath("reviews")
                                                .description("리뷰 목록"),
                                        fieldWithPath("reviews[].reviewId")
                                                .description("리뷰 ID"),
                                        fieldWithPath("reviews[].content")
                                                .description("리뷰 내용"),
                                        fieldWithPath("reviews[].reviewRating")
                                                .description("리뷰 평점"),
                                        fieldWithPath("reviews[].likeCount")
                                                .description("좋아요 수"),
                                        fieldWithPath("reviews[].hateCount")
                                                .description("싫어요 수"),
                                        fieldWithPath("reviews[].member.memberId")
                                                .description("리뷰 작성자 ID"),
                                        fieldWithPath("reviews[].member.nickname")
                                                .description("리뷰 작성자 닉네임"),
                                        fieldWithPath("reviews[].member.profileImageUrl")
                                                .description("리뷰 작성자 프로필 이미지 URL"),
                                        fieldWithPath("reviews[].storeId")
                                                .description("상점 ID"),
                                        fieldWithPath("reviews[].imageUrls")
                                                .description("리뷰 이미지 URL 리스트"),
                                        fieldWithPath("reviews[].createdAt")
                                                .description("리뷰 생성 시간"),
                                        fieldWithPath("reviews[].modifiedAt")
                                                .description("리뷰 수정 시간"),
                                        fieldWithPath("pageInfo")
                                                .description("페이지 정보"),
                                        fieldWithPath("pageInfo.currentPage")
                                                .description("현재 페이지"),
                                        fieldWithPath("pageInfo.totalPages")
                                                .description("전체 페이지 수"),
                                        fieldWithPath("pageInfo.pageSize")
                                                .description("페이지 크기"),
                                        fieldWithPath("pageInfo.totalSize")
                                                .description("전체 크기")
                                )
                        )
                );

    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReviewSuccess() throws Exception {
        // given
        ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest(
                "new content",
                4,
                List.of("https://image1.com", "https://image2.com", "https://image3.com")
        );

        // when
        ResultActions resultActions = performUpdateReviewRequest(reviewUpdateRequest, REVIEW_ID);

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("reviewId").description("리뷰 ID")
                                ),
                                requestFields(
                                        fieldWithPath("content")
                                                .description("리뷰 내용")
                                                .attributes(field("constraints", "NotBlank")),
                                        fieldWithPath("reviewRating")
                                                .description("리뷰 평점")
                                                .attributes(field("constraints", "NotNull, Min(1), Max(5)")),
                                        fieldWithPath("imageUrls")
                                                .description("리뷰 이미지 URL 리스트")
                                                .attributes(field("constraints", "Size(5)"))
                                )
                        )
                );
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReviewSuccess() throws Exception {
        // when
        ResultActions resultActions = performDeleteReviewRequest(REVIEW_ID);

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("Access Token")
                                ),
                                pathParameters(
                                        parameterWithName("reviewId").description("리뷰 ID")
                                )
                        )
                );
    }

    @Nested
    @DisplayName("리뷰 생성 실패 - 데이터 검증")
    class CreateReviewFail {

        @Test
        @DisplayName("리뷰 내용이 없는 경우")
        void createReviewFailNoContent() throws Exception {
            // given
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    "",
                    5,
                    List.of("https://image1.com", "https://image2.com", "https://image3.com")
            );

            // when
            ResultActions resultActions = performCreateReviewRequest(reviewCreateRequest, STORE_ID);

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("content : 내용을 입력해주세요. (request value: )"));

            then(reviewService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("리뷰 평점이 없는 경우")
        void createReviewFailNoReviewRating() throws Exception {
            // given
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    "content",
                    null,
                    List.of("https://image1.com", "https://image2.com", "https://image3.com")
            );

            // when
            ResultActions resultActions = performCreateReviewRequest(reviewCreateRequest, STORE_ID);

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("reviewRating : 평점을 입력해주세요. (request value: null)"));

            then(reviewService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("리뷰 평점이 1 미만인 경우")
        void createReviewFailInvalidReviewRating() throws Exception {
            // given
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    "content",
                    0,
                    List.of("https://image1.com", "https://image2.com", "https://image3.com")
            );

            // when
            ResultActions resultActions = performCreateReviewRequest(reviewCreateRequest, STORE_ID);

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("reviewRating : 평점은 1~5 사이여야 합니다. (request value: 0)"));

            then(reviewService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("리뷰 평점이 5 초과인 경우")
        void createReviewFailOverReviewRating() throws Exception {
            // given
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    "content",
                    6,
                    List.of("https://image1.com", "https://image2.com", "https://image3.com")
            );

            // when
            ResultActions resultActions = performCreateReviewRequest(reviewCreateRequest, STORE_ID);

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("reviewRating : 평점은 1~5 사이여야 합니다. (request value: 6)"));

            then(reviewService).shouldHaveNoInteractions();
        }


        @Test
        @DisplayName("리뷰 이미지 URL이 5개 초과인 경우")
        void createReviewFailOverImageUrls() throws Exception {
            // given
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    "content",
                    5,
                    List.of("https://image1.com", "https://image2.com", "https://image3.com", "https://image4.com", "https://image5.com", "https://image6.com")
            );

            // when
            ResultActions resultActions = performCreateReviewRequest(reviewCreateRequest, STORE_ID);

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("imageUrls : 이미지는 최대 5개까지 첨부할 수 있습니다. (request value: [https://image1.com, https://image2.com, https://image3.com, https://image4.com, https://image5.com, https://image6.com])"));

            then(reviewService).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("리뷰 수정 실패 - 데이터 검증")
    class UpdateReviewFail {

        @Test
        @DisplayName("리뷰 내용이 없는 경우")
        void updateReviewFailNoContent() throws Exception {
            // given
            ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest(
                    "",
                    5,
                    List.of("https://image1.com", "https://image2.com", "https://image3.com")
            );

            // when
            ResultActions resultActions = performUpdateReviewRequest(reviewUpdateRequest, REVIEW_ID);

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("content : 내용을 입력해주세요. (request value: )"));

            then(reviewService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("리뷰 평점이 없는 경우")
        void updateReviewFailNoReviewRating() throws Exception {
            // given
            ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest(
                    "content",
                    null,
                    List.of("https://image1.com", "https://image2.com", "https://image3.com")
            );

            // when
            ResultActions resultActions = performUpdateReviewRequest(reviewUpdateRequest, REVIEW_ID);

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("reviewRating : 평점을 입력해주세요. (request value: null)"));

            then(reviewService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("리뷰 평점이 1 미만인 경우")
        void updateReviewFailInvalidReviewRating() throws Exception {
            // given
            ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest(
                    "content",
                    0,
                    List.of("https://image1.com", "https://image2.com", "https://image3.com")
            );

            // when
            ResultActions resultActions = performUpdateReviewRequest(reviewUpdateRequest, REVIEW_ID);

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("reviewRating : 평점은 1~5 사이여야 합니다. (request value: 0)"));

            then(reviewService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("리뷰 평점이 5초과인 경우")
        void updateReviewFailOverReviewRating() throws Exception {
            // given
            ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest(
                    "content",
                    6,
                    List.of("https://image1.com", "https://image2.com", "https://image3.com")
            );

            // when
            ResultActions resultActions = performUpdateReviewRequest(reviewUpdateRequest, REVIEW_ID);

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("reviewRating : 평점은 1~5 사이여야 합니다. (request value: 6)"));

            then(reviewService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("리뷰 이미지 URL이 5개 초과인 경우")
        void updateReviewFailOverImageUrls() throws Exception {
            // given
            ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest(
                    "content",
                    5,
                    List.of("https://image1.com", "https://image2.com", "https://image3.com", "https://image4.com", "https://image5.com", "https://image6.com")
            );

            // when
            ResultActions resultActions = performUpdateReviewRequest(reviewUpdateRequest, REVIEW_ID);

            // then
            resultActions
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("imageUrls : 이미지는 최대 5개까지 첨부할 수 있습니다. (request value: [https://image1.com, https://image2.com, https://image3.com, https://image4.com, https://image5.com, https://image6.com])"));

            then(reviewService).shouldHaveNoInteractions();
        }

    }

}