package com.pd.gilgeorigoreuda.member.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemberReviewListResponse {

    List<MemberReviewResponse> reviews;

    private MemberReviewListResponse(final List<MemberReviewResponse> reviews) {
        this.reviews = reviews;
    }

    public static MemberReviewListResponse of(List<MemberReviewResponse> reviews) {
        return new MemberReviewListResponse(reviews);
    }

}
