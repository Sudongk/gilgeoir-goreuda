package com.pd.gilgeorigoreuda.store.repository;

import com.pd.gilgeorigoreuda.store.domain.entity.Store;
import com.pd.gilgeorigoreuda.store.domain.entity.StoreReportHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreReportHistoryRepository extends JpaRepository<StoreReportHistory, Long> {


	@Query("select sr "
			+ "from StoreReportHistory sr "
			+ "left join fetch sr.member m "
			+ "where sr.id = :reportId "
	)
	Optional<StoreReportHistory> findReportWithMemberConditionReportId(@Param("reportId") Long reportId);

	@Query("select sr "
			+ "from StoreReportHistory sr "
			+ "left join fetch sr.member m "
			+ "left join fetch sr.store "
			+ "where sr.member.id = :memberId ")
	List<StoreReportHistory> findReportWithMemberConditionMemberId(@Param("memberId") Long memberId);

}
