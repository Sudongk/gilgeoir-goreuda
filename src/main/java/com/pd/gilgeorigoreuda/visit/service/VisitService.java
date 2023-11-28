package com.pd.gilgeorigoreuda.visit.service;import com.pd.gilgeorigoreuda.common.util.DistanceCalculator;import com.pd.gilgeorigoreuda.store.domain.entity.Store;import com.pd.gilgeorigoreuda.store.domain.entity.StoreVisitRecord;import com.pd.gilgeorigoreuda.store.exception.NoSuchStoreException;import com.pd.gilgeorigoreuda.store.repository.StoreRepository;import com.pd.gilgeorigoreuda.visit.dto.request.VisitRequest;import com.pd.gilgeorigoreuda.visit.dto.request.VisitVerifyRequest;import com.pd.gilgeorigoreuda.visit.dto.response.VisitResponse;import com.pd.gilgeorigoreuda.visit.exception.DuplicatedVisitRecordException;import com.pd.gilgeorigoreuda.visit.exception.NoSuchStoreVisitRecordException;import com.pd.gilgeorigoreuda.visit.exception.OutOfBoundaryException;import com.pd.gilgeorigoreuda.visit.exception.TooLongDistanceException;import com.pd.gilgeorigoreuda.visit.repository.StoreVisitRecordRepository;import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.math.BigDecimal;@Service@Transactional(readOnly = true)@RequiredArgsConstructorpublic class VisitService {    private final StoreVisitRecordRepository storeVisitRecordRepository;    private final StoreRepository storeRepository;    private static final int BOUNDARY_METER = 300;    private static final int VALID_TIME_HOUR = 2;    public static final int MAX_APPROXIMATE_WALKING_TIME_HOUR = 4;    @Transactional    public VisitResponse visit(final Long memberId, final Long storeId, final VisitRequest visitRequest) {        Store targetStore = getTargetStore(storeId);        checkIsDuplicateVisitRecord(memberId, storeId);        BigDecimal memberLat = visitRequest.getLat();        BigDecimal memberLng = visitRequest.getLng();        BigDecimal targetStoreLat = targetStore.getLat();        BigDecimal targetStoreLng = targetStore.getLng();        int walkingDistance = getDistanceBetweenStoreAndMember(memberLat, memberLng, targetStoreLat, targetStoreLng);        int approximateWalkingTime = getApproximateWalkingTime(walkingDistance);        StoreVisitRecord savedStoreVisitRecord = storeVisitRecordRepository.save(                StoreVisitRecord.from(memberId, storeId, walkingDistance)        );        return VisitResponse.of(savedStoreVisitRecord, approximateWalkingTime);    }    private void checkIsDuplicateVisitRecord(final Long memberId, final Long storeId) {        storeVisitRecordRepository.findByMemberIdAndStoreId(memberId, storeId)                .ifPresent(storeVisitRecord -> {                    throw new DuplicatedVisitRecordException();                });    }    @Transactional    public void verifyVisit(final Long memberId, final Long storeId, final VisitVerifyRequest visitVerifyRequest) {        Store targetStore = getTargetStore(storeId);        BigDecimal memberLat = visitVerifyRequest.getLat();        BigDecimal memberLng = visitVerifyRequest.getLng();        BigDecimal targetStoreLat = targetStore.getLat();        BigDecimal targetStoreLng = targetStore.getLng();        isMemberInBoundary(memberLat, memberLng, targetStoreLat, targetStoreLng);        StoreVisitRecord storeVisitRecord = storeVisitRecordRepository.findByMemberIdAndStoreId(memberId, storeId)                .orElseThrow(NoSuchStoreVisitRecordException::new);        storeVisitRecord.checkTimeOut(VALID_TIME_HOUR);        storeVisitRecord.verifyVisit();    }    private static void isMemberInBoundary(final BigDecimal memberLat, final BigDecimal memberLng,                                           final BigDecimal targetStoreLat, final BigDecimal targetStoreLng) {        int distance = DistanceCalculator.calculateDistance(memberLat, memberLng, targetStoreLat, targetStoreLng);        if (distance > BOUNDARY_METER) {            throw new OutOfBoundaryException();        }    }    private static int getDistanceBetweenStoreAndMember(final BigDecimal memberLat, final BigDecimal memberLng,                                                        final BigDecimal targetStoreLat, final BigDecimal targetStoreLng) {        return DistanceCalculator.calculateDistance(memberLat, memberLng, targetStoreLat, targetStoreLng);    }    private static int getApproximateWalkingTime(final int walkingDistance) {        int approximateWalkingTime = DistanceCalculator.calculateApproximateWalkingTime(walkingDistance);        if (approximateWalkingTime > MAX_APPROXIMATE_WALKING_TIME_HOUR) {            throw new TooLongDistanceException();        }        return approximateWalkingTime;    }    private Store getTargetStore(final Long storeId) {        return storeRepository.findById(storeId)                .orElseThrow(NoSuchStoreException::new);    }}