package com.pd.gilgeorigoreuda.store.repository;

import com.pd.gilgeorigoreuda.member.domain.entity.Member;
import com.pd.gilgeorigoreuda.settings.RepositoryTest;
import com.pd.gilgeorigoreuda.store.domain.entity.Store;
import com.pd.gilgeorigoreuda.store.domain.entity.StoreReportHistory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.pd.gilgeorigoreuda.settings.fixtures.MemberFixtures.*;
import static com.pd.gilgeorigoreuda.settings.fixtures.StoreFixtures.*;
import static com.pd.gilgeorigoreuda.settings.fixtures.StoreReportHistoryFixtures.*;

import static org.assertj.core.api.SoftAssertions.*;
import static org.assertj.core.api.Assertions.*;

class StoreReportHistoryRepositoryTest extends RepositoryTest {

    @Test
    @DisplayName("신고 아이디로 해당 신고와 신고자를 함께 조회")
    void findStoreReportHistoryByReportId() {
        // given
        Member KIM = dataBuilder.buildMember(KIM());
        Member LEE = dataBuilder.buildMember(LEE());
        Store BUNGEOPPANG = dataBuilder.buildStore(BUNGEOPPANG());
        StoreReportHistory BUNGEOPPANG_LEE_REPORT_HISTORY = dataBuilder.buildStoreReportHistory(BUNGEOPPANG_LEE_REPORT_HISTORY());

        // when
        StoreReportHistory storeReportHistory = storeReportHistoryRepository.findStoreReportHistoryByReportId(BUNGEOPPANG_LEE_REPORT_HISTORY.getId()).get();

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(storeReportHistory.getId()).isEqualTo(BUNGEOPPANG_LEE_REPORT_HISTORY.getId());
                    softly.assertThat(storeReportHistory.getMember().getId()).isEqualTo(LEE.getId());
                    softly.assertThat(storeReportHistory.getStore().getId()).isEqualTo(BUNGEOPPANG.getId());
                }
        );
    }

    @Test
    @DisplayName("회원 아이디로 해당 회원이 신고한 신고들을 조회")
    void findStoreReportHistoriesByMemberId() {
        // given
        Member KIM = dataBuilder.buildMember(KIM());
        Member LEE = dataBuilder.buildMember(LEE());

        Store BUNGEOPPANG = dataBuilder.buildStore(BUNGEOPPANG());
        Store TTEOKBOKKI = dataBuilder.buildStore(TTEOKBOKKI());

        StoreReportHistory BUNGEOPPANG_LEE_REPORT_HISTORY = dataBuilder.buildStoreReportHistory(BUNGEOPPANG_LEE_REPORT_HISTORY());
        StoreReportHistory TTEOKBOKKI_LEE_REPORT_HISTORY = dataBuilder.buildStoreReportHistory(TTEOKBOKKI_LEE_REPORT_HISTORY());

        // when
        List<StoreReportHistory> storeReportHistories = storeReportHistoryRepository.findStoreReportHistoriesByMemberId(LEE.getId());

        // then
        assertThat(storeReportHistories.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("가게 아이디로 해당 가게에 대한 신고들을 조회")
    void findStoreReportHistoriesByStoreId() {
        // given
        Member KIM = dataBuilder.buildMember(KIM());
        Member LEE = dataBuilder.buildMember(LEE());

        Store BUNGEOPPANG = dataBuilder.buildStore(BUNGEOPPANG());
        Store TTEOKBOKKI = dataBuilder.buildStore(TTEOKBOKKI());

        StoreReportHistory BUNGEOPPANG_LEE_REPORT_HISTORY = dataBuilder.buildStoreReportHistory(BUNGEOPPANG_LEE_REPORT_HISTORY());
        StoreReportHistory TTEOKBOKKI_LEE_REPORT_HISTORY = dataBuilder.buildStoreReportHistory(TTEOKBOKKI_LEE_REPORT_HISTORY());

        // when
        List<StoreReportHistory> storeReportHistories = storeReportHistoryRepository.findStoreReportHistoriesByStoreId(BUNGEOPPANG.getId());

        // then
        assertThat(storeReportHistories.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("가게 아이디와 회원 아이디로 해당 가게에 대한 회원의 신고를 조회")
    void findStoreReportHistoryByStoreIdAndMemberId() {
        // given
        Member KIM = dataBuilder.buildMember(KIM());
        Member LEE = dataBuilder.buildMember(LEE());

        Store BUNGEOPPANG = dataBuilder.buildStore(BUNGEOPPANG());
        Store TTEOKBOKKI = dataBuilder.buildStore(TTEOKBOKKI());

        StoreReportHistory BUNGEOPPANG_LEE_REPORT_HISTORY = dataBuilder.buildStoreReportHistory(BUNGEOPPANG_LEE_REPORT_HISTORY());
        StoreReportHistory TTEOKBOKKI_LEE_REPORT_HISTORY = dataBuilder.buildStoreReportHistory(TTEOKBOKKI_LEE_REPORT_HISTORY());

        // when
        StoreReportHistory storeReportHistory = storeReportHistoryRepository.findStoreReportHistoryByStoreIdAndMemberId(BUNGEOPPANG.getId(), LEE.getId()).get();

        // then
        assertSoftly(
                softly -> {
                    softly.assertThat(storeReportHistory.getStore().getId()).isEqualTo(BUNGEOPPANG.getId());
                    softly.assertThat(storeReportHistory.getMember().getId()).isEqualTo(LEE.getId());
                }
        );
    }


}