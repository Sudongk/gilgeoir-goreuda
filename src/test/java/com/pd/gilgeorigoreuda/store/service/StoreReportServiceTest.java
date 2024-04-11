package com.pd.gilgeorigoreuda.store.service;

import com.pd.gilgeorigoreuda.member.domain.entity.Member;
import com.pd.gilgeorigoreuda.settings.ServiceTest;
import com.pd.gilgeorigoreuda.store.domain.entity.Store;
import com.pd.gilgeorigoreuda.store.domain.entity.StoreReportHistory;
import com.pd.gilgeorigoreuda.store.dto.request.ReportCreateRequest;
import com.pd.gilgeorigoreuda.store.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static com.pd.gilgeorigoreuda.settings.fixtures.MemberFixtures.*;
import static com.pd.gilgeorigoreuda.settings.fixtures.StoreFixtures.*;
import static com.pd.gilgeorigoreuda.settings.fixtures.StoreReportHistoryFixtures.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class StoreReportServiceTest extends ServiceTest {

    private static final Integer VALID_BOUNDARY_10M = 10;
    private static final Integer INVALID_BOUNDARY_200M = 200;

    private ReportCreateRequest makeReportCreateRequest() {
        return new ReportCreateRequest(
                "report content",
                BigDecimal.valueOf(37.123456),
                BigDecimal.valueOf(127.123456)
        );
    }

    @Test
    @DisplayName("가게 신고 생성 성공 - 가게 신고 이력이 없는 경우, 해당 가게가 존재하는 경우, 유효 거리인 경우")
    void createStoreReportSuccess() {
        // given
        ReportCreateRequest reportCreateRequest = makeReportCreateRequest();
        Store bungeoppang = BUNGEOPPANG();
        Member lee = LEE();

        given(storeReportHistoryRepository.findStoreReportHistoryByStoreIdAndMemberId(bungeoppang.getId(), lee.getId()))
                .willReturn(Optional.empty());

        given(storeRepository.findById(bungeoppang.getId()))
                .willReturn(Optional.of(bungeoppang));

        given(distanceCalculator.getDistance(
                reportCreateRequest.getMemberLat(),
                reportCreateRequest.getMemberLng(),
                bungeoppang.getLat(),
                bungeoppang.getLng()
        )).willReturn(VALID_BOUNDARY_10M);

        // when
        storeReportService.createStoreReport(reportCreateRequest, bungeoppang.getId(), lee.getId());

        // then
        then(storeReportHistoryRepository).should(times(1)).findStoreReportHistoryByStoreIdAndMemberId(anyLong(), anyLong());
        then(storeRepository).should(times(1)).findById(anyLong());
        then(distanceCalculator).should(times(1)).getDistance(
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class),
                any(BigDecimal.class)
        );
        then(storeReportHistoryRepository).should(times(1)).save(any());
    }

    @Nested
    @DisplayName("가게 신고 생성 실패 케이스")
    class createStoreReportFailCases {

        @Test
        @DisplayName("작성자가 이미 해당 가게 신고 이력이 이미 존재하는 경우")
        void createStoreReportFailByExistStoreReportHistory() {
            // given
            ReportCreateRequest reportCreateRequest = makeReportCreateRequest();
            Store bungeoppang = BUNGEOPPANG();
            Member lee = LEE();
            StoreReportHistory bungeoppang_lee_report_history = BUNGEOPPANG_LEE_REPORT_HISTORY();


            given(storeReportHistoryRepository.findStoreReportHistoryByStoreIdAndMemberId(bungeoppang.getId(), lee.getId()))
                    .willReturn(Optional.of(bungeoppang_lee_report_history));

            // when & then
            assertThatThrownBy(() -> storeReportService.createStoreReport(reportCreateRequest, bungeoppang.getId(), lee.getId()))
                    .isInstanceOf(AlreadyReportedException.class)
                    .extracting("errorCode")
                    .isEqualTo("R004");

            then(storeReportHistoryRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("해당 가게가 존재하지 않는 경우")
        void createStoreReportFailByNotExistStore() {
            // given
            ReportCreateRequest reportCreateRequest = makeReportCreateRequest();
            Store bungeoppang = BUNGEOPPANG();
            Member lee = LEE();

            given(storeReportHistoryRepository.findStoreReportHistoryByStoreIdAndMemberId(bungeoppang.getId(), lee.getId()))
                    .willReturn(Optional.empty());

            given(storeRepository.findById(bungeoppang.getId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> storeReportService.createStoreReport(reportCreateRequest, bungeoppang.getId(), lee.getId()))
                    .isInstanceOf(NoSuchStoreException.class)
                    .extracting("errorCode")
                    .isEqualTo("S001");

            then(storeReportHistoryRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("유효 거리가 아닌 경우")
        void createStoreReportFailByInvalidDistance() {
            // given
            ReportCreateRequest reportCreateRequest = makeReportCreateRequest();
            Store bungeoppang = BUNGEOPPANG();
            Member lee = LEE();

            given(storeReportHistoryRepository.findStoreReportHistoryByStoreIdAndMemberId(bungeoppang.getId(), lee.getId()))
                    .willReturn(Optional.empty());

            given(storeRepository.findById(bungeoppang.getId()))
                    .willReturn(Optional.of(bungeoppang));

            given(distanceCalculator.getDistance(
                    reportCreateRequest.getMemberLat(),
                    reportCreateRequest.getMemberLng(),
                    bungeoppang.getLat(),
                    bungeoppang.getLng()
            )).willReturn(INVALID_BOUNDARY_200M);

            // when & then
            assertThatThrownBy(() -> storeReportService.createStoreReport(reportCreateRequest, bungeoppang.getId(), lee.getId()))
                    .isInstanceOf(TooLongDistanceToReportException.class)
                    .extracting("errorCode")
                    .isEqualTo("R003");

            then(storeReportHistoryRepository).should(never()).save(any());
        }
    }

    @Test
    @DisplayName("가게 신고 삭제 성공 - 해당 신고자가 신고한 경우")
    void deleteReportSuccess() {
        // given
        StoreReportHistory bungeoppang_lee_report_history = BUNGEOPPANG_LEE_REPORT_HISTORY();
        Member lee = LEE();

        given(storeReportHistoryRepository.findStoreReportHistoryByReportId(bungeoppang_lee_report_history.getId()))
                .willReturn(Optional.of(bungeoppang_lee_report_history));

        // when
        storeReportService.deleteReport(bungeoppang_lee_report_history.getId(), lee.getId());

        // then
        then(storeReportHistoryRepository).should(times(1)).findStoreReportHistoryByReportId(anyLong());
        then(storeReportHistoryRepository).should(times(1)).deleteById(anyLong());
    }

    @Nested
    @DisplayName("가게 신고 삭제 실패 케이스")
    class deleteReportFailCases {

        @Test
        @DisplayName("해당 신고자가 신고한 경우가 아닌 경우")
        void deleteReportFailByNotReporter() {
            // given
            StoreReportHistory bungeoppang_lee_report_history = BUNGEOPPANG_LEE_REPORT_HISTORY();
            Member notReporterMember = KIM();

            given(storeReportHistoryRepository.findStoreReportHistoryByReportId(bungeoppang_lee_report_history.getId()))
                    .willReturn(Optional.of(bungeoppang_lee_report_history));

            // when & then
            assertThatThrownBy(() -> storeReportService.deleteReport(bungeoppang_lee_report_history.getId(), notReporterMember.getId()))
                    .isInstanceOf(NoReporterMemberException.class)
                    .extracting("errorCode")
                    .isEqualTo("S006");

            then(storeReportHistoryRepository).should(never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("해당 신고 이력이 존재하지 않는 경우")
        void deleteReportFailByNotExistReport() {
            // given
            StoreReportHistory bungeoppang_lee_report_history = BUNGEOPPANG_LEE_REPORT_HISTORY();
            Member lee = LEE();

            given(storeReportHistoryRepository.findStoreReportHistoryByReportId(bungeoppang_lee_report_history.getId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> storeReportService.deleteReport(bungeoppang_lee_report_history.getId(), lee.getId()))
                    .isInstanceOf(NoSuchReportException.class)
                    .extracting("errorCode")
                    .isEqualTo("R002");

            then(storeReportHistoryRepository).should(never()).deleteById(anyLong());
        }

    }

}