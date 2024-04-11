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

}
