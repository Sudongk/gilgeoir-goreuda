package com.pd.gilgeorigoreuda.review.controller;

import com.pd.gilgeorigoreuda.auth.annotation.MemberInfo;
import com.pd.gilgeorigoreuda.auth.annotation.MemberOnly;
import com.pd.gilgeorigoreuda.auth.domain.LoginMember;
import com.pd.gilgeorigoreuda.review.dto.request.ReviewCommentCreateRequest;
import com.pd.gilgeorigoreuda.review.dto.request.ReviewCreateRequest;
import com.pd.gilgeorigoreuda.review.dto.request.ReviewUpdateRequest;
import com.pd.gilgeorigoreuda.review.dto.response.ReviewCommentListResponse;
import com.pd.gilgeorigoreuda.review.dto.response.ReviewCreateResponse;
import com.pd.gilgeorigoreuda.review.dto.response.ReviewListResponse;
import com.pd.gilgeorigoreuda.review.service.ReviewCommentService;
import com.pd.gilgeorigoreuda.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    private final ReviewCommentService commentService;

    @MemberOnly
    @PostMapping(value = "/stores/{storeId}")
    public ResponseEntity<Void> createReview(
            @PathVariable("storeId") final Long storeId,
            @MemberInfo final LoginMember loginMember,
            @RequestBody @Valid final ReviewCreateRequest request
    ) {
        ReviewCreateResponse response = reviewService.createReview(storeId, loginMember.getMemberId(), request);

        return ResponseEntity
                .created(URI.create("/api/v1/reviews/stores/" + storeId + "/reviews/" + response.getId()))
                .build();
    }

    @GetMapping("/stores/{storeId}")
    public ResponseEntity<ReviewListResponse> getReviews(
            @PathVariable final Long storeId,
            final Pageable pageable
    ) {
        ReviewListResponse response = reviewService.findReviewsByStoreId(storeId, pageable);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @MemberOnly
    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable("reviewId") final Long reviewId,
            @MemberInfo final LoginMember loginMember,
            @RequestBody @Valid final ReviewUpdateRequest reviewRequest
    ) {
        reviewService.updateReview(reviewId, loginMember.getMemberId(), reviewRequest);

        return ResponseEntity
                .ok()
                .build();
    }

    @MemberOnly
    @DeleteMapping("{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable("reviewId") final Long reviewId,
            @MemberInfo final LoginMember loginMember
    ) {
        reviewService.deleteReview(reviewId, loginMember.getMemberId());

        return ResponseEntity
                .ok()
                .build();
    }

    @MemberOnly
    @PostMapping("{reviewId}/comments")
    public ResponseEntity<Void> createReviewComment(
            @PathVariable("reviewId") final Long reviewId,
            @MemberInfo final LoginMember loginMember,
            @RequestBody @Valid final ReviewCommentCreateRequest commentRequest
    ) {
        commentService.saveComment(reviewId, loginMember.getMemberId(), commentRequest);

        return ResponseEntity
                .ok()
                .build();
    }

    @GetMapping("{reviewId}/comments")
    public ResponseEntity<ReviewCommentListResponse> getReviewComments(
            @PathVariable("reviewId") final Long reviewId,
            @RequestParam(name = "page", required = false, defaultValue = "0") final Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") final Integer size
    ) {
        ReviewCommentListResponse response = commentService.findCommentsByReviewId(reviewId,
                PageRequest.of(page, size));

        return ResponseEntity
                .ok()
                .body(response);
    }

    @MemberOnly
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateReviewComment(
            @PathVariable("commentId") final Long commentId,
            @MemberInfo final LoginMember loginMember,
            @RequestBody @Valid final ReviewCommentCreateRequest commentRequest
    ) {
        commentService.updateComment(commentId, loginMember.getMemberId(), commentRequest);

        return ResponseEntity
                .ok()
                .build();
    }

    @MemberOnly
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteReviewComment(
            @PathVariable("commentId") final Long commentId,
            @MemberInfo final LoginMember loginMember
    ) {
        commentService.deleteReviewComment(commentId, loginMember.getMemberId());

        return ResponseEntity
                .ok()
                .build();
    }

}