package com.pd.gilgeorigoreuda.review.dto.response;

import com.pd.gilgeorigoreuda.review.domain.entity.ReviewComment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewCommentResponse {

    private Long reviewCommentId;
    private String content;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdAt;

    public ReviewCommentResponse(
            final Long reviewCommentId,
            final String content,
            final String nickname,
            final String profileImageUrl,
            final LocalDateTime createdAt
    ) {
        this.reviewCommentId = reviewCommentId;
        this.content = content;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
    }

    public static ReviewCommentResponse of(final ReviewComment reviewComment) {
        return new ReviewCommentResponse(
            reviewComment.getId(),
            reviewComment.getContent(),
            reviewComment.getMember().getNickname(),
            reviewComment.getMember().getProfileImageUrl(),
            reviewComment.getCreatedAt()
        );
    }

}
