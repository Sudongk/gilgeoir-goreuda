package com.pd.gilgeorigoreuda.settings.fixtures;

import com.pd.gilgeorigoreuda.store.domain.entity.StoreReportHistory;

import static com.pd.gilgeorigoreuda.settings.fixtures.MemberFixtures.*;
import static com.pd.gilgeorigoreuda.settings.fixtures.StoreFixtures.*;

public class StoreReportHistoryFixtures {

    public static StoreReportHistory BUNGEOPPANG_LEE_REPORT_HISTORY() {
        return StoreReportHistory.builder()
                .id(1L)
                .content("report content")
                .member(LEE())
                .store(BUNGEOPPANG())
                .build();
    }

    public static StoreReportHistory ODENG_KIM_REPORT_HISTORY() {
        return StoreReportHistory.builder()
                .id(2L)
                .content("report content")
                .member(KIM())
                .store(ODENG())
                .build();
    }

    public static StoreReportHistory TTEOKBOKKI_LEE_REPORT_HISTORY() {
        return StoreReportHistory.builder()
                .id(3L)
                .content("report content")
                .member(LEE())
                .store(TTEOKBOKKI())
                .build();
    }

    public static StoreReportHistory BUNGEOPPANG_PARK_REPORT_HISTORY() {
        return StoreReportHistory.builder()
                .id(4L)
                .content("report content")
                .member(PARK())
                .store(BUNGEOPPANG())
                .build();
    }

}
