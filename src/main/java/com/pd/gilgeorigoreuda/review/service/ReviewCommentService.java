package com.pd.gilgeorigoreuda.review.service;

import com.pd.gilgeorigoreuda.member.domain.entity.Member;
import com.pd.gilgeorigoreuda.review.domain.entity.Review;
import com.pd.gilgeorigoreuda.review.domain.entity.ReviewComment;
import com.pd.gilgeorigoreuda.review.dto.request.ReviewCommentRequest;
import com.pd.gilgeorigoreuda.review.dto.response.ReviewCommentResponse;
import com.pd.gilgeorigoreuda.review.repository.ReviewCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewCommentService {
    
    private final ReviewCommentRepository commentRepository;
    @Transactional
    public void saveComment(Long reviewId, Long memberId , ReviewCommentRequest request) {
        ReviewComment comment = ReviewComment.builder()
                .content(request.getContent())
                .review(Review.builder().id(reviewId).build())
                .member(Member.builder().id(memberId).build())
                .build();
        commentRepository.save(comment);
    }

    public Page<ReviewCommentResponse> findCommentsByReviewId(Long reviewId, Pageable pageable) {
        Page<ReviewComment> comments = commentRepository.findByReview_Id(reviewId, pageable);
        return comments.map(comment -> new ReviewCommentResponse(comment));
    }
}
