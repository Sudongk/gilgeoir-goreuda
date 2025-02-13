package com.pd.gilgeorigoreuda.settings.fixtures;

import com.pd.gilgeorigoreuda.member.domain.entity.MemberActiveInfo;
import com.pd.gilgeorigoreuda.member.domain.entity.MemberLevel;

import static com.pd.gilgeorigoreuda.member.domain.entity.MemberLevel.*;

public class MemberActiveInfoFixtures {

    public static MemberActiveInfo KIM_ACTIVE_INFO() {
        return MemberActiveInfo.builder()
                .id(1L)
                .memberLevel(BEGINNER)
                .totalWalkingDistance(1000)
                .totalVisitCount(10)
                .exp(100)
                .build();
    }

    public static MemberActiveInfo LEE_ACTIVE_INFO() {
        return MemberActiveInfo.builder()
                .id(2L)
                .memberLevel(BEGINNER)
                .totalWalkingDistance(1000)
                .totalVisitCount(10)
                .exp(100)
                .build();
    }

    public static MemberActiveInfo PARK_ACTIVE_INFO() {
        return MemberActiveInfo.builder()
                .id(3L)
                .memberLevel(BEGINNER)
                .totalWalkingDistance(1000)
                .totalVisitCount(10)
                .exp(100)
                .build();
    }

}
