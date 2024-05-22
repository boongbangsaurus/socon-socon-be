package site.soconsocon.socon.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soconsocon.socon.global.domain.ErrorCode;
import site.soconsocon.socon.global.exception.SoconException;
import site.soconsocon.socon.search.domain.dto.request.StoreCreateDocument;
import site.soconsocon.socon.search.service.SearchService;
import site.soconsocon.socon.store.domain.dto.request.*;
import site.soconsocon.socon.store.domain.dto.response.*;
import site.soconsocon.socon.store.domain.entity.feign.Member;
import site.soconsocon.socon.store.domain.entity.jpa.*;
import site.soconsocon.socon.store.exception.StoreErrorCode;
import site.soconsocon.socon.store.exception.StoreException;
import site.soconsocon.socon.store.feign.FeignServiceClient;
import site.soconsocon.socon.store.repository.jpa.*;

import java.sql.Time;
import java.time.LocalDate;
import java.util.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class StoreService {

    private final SearchService searchService;

    private final StoreRepository storeRepository;
    private final BusinessRegistrationRepository businessRegistrationRepository;
    private final BusinessHourRepository businessHourRepository;
    private final FavStoreRepository favStoreRepository;
    private final IssueRepository issueRepository;
    private final SoconRepository soconRepository;
    private final FeignServiceClient feignServiceClient;

    // 사업자 번호 목록 조회
    public List<GetBusinessNumberListResponse> getBusinessNumberList(int memberId) {

        List<GetBusinessNumberListResponse> response = new ArrayList<>();
        List<BusinessRegistration> result = businessRegistrationRepository.getBusinessNumberList(memberId);
        for(BusinessRegistration regi : result){
            response.add(GetBusinessNumberListResponse.builder()
                    .registrationNumber(regi.getRegistrationNumber())
                    .id(regi.getId())
                    .build());
        }

        return response;
    }

    @Transactional
    // 가게 정보 등록
    public void saveStore(AddStoreRequest request, int memberId) {

        //RegistrationNumber 조회
        BusinessRegistration businessRegistration = businessRegistrationRepository.findById(request.getRegistrationNumberId()).orElseThrow(() -> new StoreException(StoreErrorCode.REGISTRATION_NUMBER_NOT_FOUND));

        // 본인 소유의 사업자 등록 id가 아닌 경우
        if (!(businessRegistration.getMemberId().equals(memberId))) {
            throw new SoconException(ErrorCode.FORBIDDEN);
        }

        Store store = Store.builder()
                .name(request.getName())
                .category(request.getCategory())
                .image(request.getImage())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .lat(request.getLat())
                .lng(request.getLng())
                .introduction(request.getIntroduction())
                .closingPlanned(null).isClosed(false)
                .createdAt(LocalDate.now())
                .memberId(memberId)
                .businessRegistration(businessRegistration).build();

        // 중복체크 : store name, registrationNumber, lat, lng
        if (storeRepository.checkStoreDuplication(store.getName(), store.getBusinessRegistration().getId(), store.getLat(), store.getLng()) > 0) {
            throw new StoreException(StoreErrorCode.ALREADY_SAVED_STORE);
        }

        try {
            storeRepository.save(store);
            searchService.addStores(StoreCreateDocument.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .category(store.getCategory())
                    .phoneNumber(store.getPhoneNumber())
                    .lat(store.getLat())
                    .lng(store.getLng())
                    .address(store.getAddress())
                    .introduction(store.getIntroduction())
                    .build());
        }catch (RuntimeException e){
            throw new StoreException(StoreErrorCode.TRANSACTION_FAIL);
        }

        // businessHourList 저장
        List<BusinessHourRequest> businessHours = request.getBusinessHour();


        for (BusinessHourRequest businessHour : businessHours) {
            BusinessHour hour = new BusinessHour();
            if(businessHour.getIsWorking()){
                hour.setIsWorking(true);
                hour.setOpenAt(Time.valueOf(businessHour.getOpenAt() + ":00"));
                hour.setCloseAt(Time.valueOf(businessHour.getCloseAt() + ":00"));
            }
            else{
                hour.setIsWorking(false);
                hour.setOpenAt(null);
                hour.setCloseAt(null);
            }
            if(businessHour.getIsBreaktime()){
                hour.setIsBreaktime(true);
                hour.setBreaktimeStart(Time.valueOf(businessHour.getBreaktimeStart() + ":00"));
                hour.setBreaktimeEnd(Time.valueOf(businessHour.getBreaktimeEnd() + ":00"));
            }
            else{
                hour.setIsBreaktime(false);
                hour.setBreaktimeStart(null);
                hour.setBreaktimeEnd(null);
            }
            hour.setDay(businessHour.getDay());
            hour.setStore(store);

            businessHourRepository.save(hour);
        }
    }

    // 가게 정보 목록 조회
    public List<StoreListResponse> getStoreList(int memberId) {

        List<Store> stores = storeRepository.findStoresByMemberId(memberId);

        List<StoreListResponse> storeList = new ArrayList<>();
        for (Store store : stores) {
            storeList.add(StoreListResponse.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .category(store.getCategory())
                    .image(store.getImage())
                    .createdAt(store.getCreatedAt())
                    .build());
        }
        return storeList;
    }

    // 가게 정보 상세 조회
    public StoreInfoResponse getStoreInfo(Integer storeId) {

        Store store = storeRepository.findById(storeId).orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        Member member = feignServiceClient.getMemberInfo(store.getMemberId());

        BusinessRegistration businessRegistration = store.getBusinessRegistration();
        Integer favoriteCount = favStoreRepository.countByStoreId(storeId);

        List<BusinessHour> businessHours = businessHourRepository.findBusinessHourResponseByStoreId(storeId);
        List<BusinessHourResponse> businessHourResponses = new ArrayList<>();
        for(BusinessHour businessHour : businessHours){
            businessHourResponses.add(BusinessHourResponse.builder()
                    .day(businessHour.getDay())
                    .isWorking(businessHour.getIsWorking())
                    .isBreaktime(businessHour.getIsBreaktime())
                    .openAt(businessHour.getOpenAt())
                    .closeAt(businessHour.getCloseAt())
                    .breaktimeStart(businessHour.getBreaktimeStart())
                    .breaktimeEnd(businessHour.getBreaktimeEnd())
                    .build());
        }



        return StoreInfoResponse.builder()
                .storeId(storeId)
                .name(store.getName())
                .category(store.getCategory())
                .image(store.getImage())
                .address(store.getAddress())
                .phoneNumber(store.getPhoneNumber())
                .businessHours(businessHourResponses)
                .introduction(store.getIntroduction())
                .closingPlanned(store.getClosingPlanned())
                .favoriteCount(favoriteCount)
                .createdAt(store.getCreatedAt())
                .registrationNumber(businessRegistration.getRegistrationNumber())
                .registrationAddress(businessRegistration.getRegistrationAddress())
                .owner(member.getName())
                .build();
    }

    // 가게 정보 수정
    public void updateStoreInfo(Integer storeId, UpdateStoreInfoRequest request, int memberId) {

        var store = storeRepository.findById(storeId).orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));
        if (!store.getMemberId().equals(memberId)) {
            // 본인 가게 아닐 경우
            throw new SoconException(ErrorCode.FORBIDDEN);
        } else {
            // 영업시간 수정
            List<BusinessHourRequest> requestBusinessHours = request.getBusinessHour(); // 요청 영업시간
            List<BusinessHour> savedBusinessHours = businessHourRepository.findByStoreId(storeId); // 저장되어있는 영업시간
            if (savedBusinessHours.isEmpty()) {
                // 저장된 값이 없을 경우
                for (BusinessHourRequest businessHour : requestBusinessHours) {
                    BusinessHour hour = new BusinessHour();
                    if (businessHour.getIsWorking()) {
                        hour.setIsWorking(true);
                        hour.setOpenAt(Time.valueOf(businessHour.getOpenAt()+":00"));
                        hour.setCloseAt(Time.valueOf(businessHour.getCloseAt()+":00"));
                    }
                    else{
                        hour.setIsWorking(false);
                        hour.setOpenAt(null);
                        hour.setCloseAt(null);
                    }
                    if (businessHour.getIsBreaktime()){
                        hour.setIsBreaktime(true);
                        hour.setBreaktimeStart(Time.valueOf(businessHour.getBreaktimeStart()+":00"));
                        hour.setBreaktimeEnd(Time.valueOf(businessHour.getBreaktimeEnd()+":00"));
                    }
                    else{
                        hour.setIsBreaktime(false);
                        hour.setBreaktimeStart(null);
                        hour.setBreaktimeEnd(null);
                    }

                    hour.setDay(businessHour.getDay());
                    hour.setStore(store);

                    businessHourRepository.save(hour);
                }

                List<BusinessHour> newBusinessHours = businessHourRepository.findByStoreId(storeId);

                Store savedStore = storeRepository.findById(storeId).orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));
                savedStore.setBusinessHours(newBusinessHours);
                storeRepository.save(savedStore);
            } else {
                // 저장된 값이 있을 경우
                for (BusinessHourRequest businessHour : requestBusinessHours) {
                    BusinessHour matchedBusinessHour = savedBusinessHours.stream().filter(savedBusinessHour -> savedBusinessHour.getDay().equals(businessHour.getDay())).findFirst().orElse(null); // null일 경우

                    if(businessHour .getIsWorking()){
                        matchedBusinessHour.setIsWorking(true);
                        matchedBusinessHour.setOpenAt(Time.valueOf(businessHour.getOpenAt() + ":00"));
                        matchedBusinessHour.setCloseAt(Time.valueOf(businessHour.getCloseAt()+ ":00"));
                    }
                    else{
                        matchedBusinessHour.setIsWorking(false);
                        matchedBusinessHour.setOpenAt(null);
                        matchedBusinessHour.setCloseAt(null);
                    }
                    if(businessHour.getIsBreaktime()){
                        matchedBusinessHour.setIsBreaktime(true);
                        matchedBusinessHour.setBreaktimeStart(Time.valueOf(businessHour.getBreaktimeStart()+ ":00"));
                        matchedBusinessHour.setBreaktimeEnd(Time.valueOf(businessHour.getBreaktimeEnd()+ ":00"));

                    }else{
                        matchedBusinessHour.setIsBreaktime(false);
                        matchedBusinessHour.setBreaktimeStart(null);
                        matchedBusinessHour.setBreaktimeEnd(null);

                    }

                    businessHourRepository.save(matchedBusinessHour);
                }
            }

            store.setPhoneNumber(request.getPhoneNumber());
            store.setAddress(request.getAddress());
            store.setLat(request.getLat());
            store.setLng(request.getLng());
            store.setIntroduction(request.getIntroduction());

            storeRepository.save(store);

        }
    }

    // 가게 폐업 정보 수정
    public void updateClosedPlanned(Integer storeId, UpdateClosedPlannedRequest request, int memberId) {
        var store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        if (!Objects.equals(memberId, store.getMemberId())) {
            // 점포 소유주의 요청이 아닐 경우
            throw new SoconException(ErrorCode.FORBIDDEN);
        }
        if (store.getClosingPlanned() != null) {
            // 이미 폐업 신고가 되어 있는 경우
            throw new StoreException(StoreErrorCode.ALREADY_SET_CLOSE_PLAN);
        }
        LocalDate closedAt = LocalDate.now().plusDays(request.getCloseAfter());
        store.setClosingPlanned(closedAt);

        storeRepository.save(store);

        // 발행 중 소콘 발행 중지
        List<Issue> issues = issueRepository.findActiveIssuesByStoreId(storeId);
        for (Issue issue : issues) {
            issue.setStatus(IssueStatus.I);
            issueRepository.save(issue);
            // 발행 된 소콘 중 사용되지 않은 소콘 마감기한 업데이트
            List<Socon> socons = soconRepository.getUnusedSoconByIssueId(issue.getId());
            for (Socon socon : socons) {
                socon.setExpiredAt(closedAt.atTime(23, 59, 59));
                soconRepository.save(socon);
            }
            // 폐업신고 시 알림 발송
            storeRepository.save(store);

        }
    }

    // 폐업 상태 업데이트
    public void updateCloseStore(){
        // 폐업 예정일 지정되어있지만 폐업하지 않은 가게 리스트
        List<Store> stores = storeRepository.storesScheduledToClose();

        for(Store store : stores){
            if(store.getClosingPlanned().isEqual(LocalDate.now())){
                // 오늘 일자와 같다면 폐업 처리
                store.setIsClosed(true);
                storeRepository.save(store);

                // 관심 가게 목록에서 삭제
                favStoreRepository.deleteByStoreId(store.getId());

                // 사용되지 않은 소콘 상태 업데이트
                List<Socon> socons = soconRepository.getSoconByStoreId(store.getId());
                for(Socon socon : socons ){
                    socon.setStatus(SoconStatus.expired);
                    soconRepository.save(socon);
                }
            }
        }
    }

    // 관심가게 추가,취소
    public void favoriteStore(Integer storeId, int memberId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));
        if (Boolean.TRUE.equals(store.getIsClosed())) {
            // 폐업상태일 경우
            throw new SoconException(ErrorCode.BAD_REQUEST);
        }
        FavStore favStore = favStoreRepository.findByMemberIdAndStoreId(memberId, storeId);

        if (favStore != null) {
            // 이미 좋아요 한 경우
            favStoreRepository.delete(favStore);
        } else {
            favStoreRepository.save(FavStore.builder().memberId(memberId).storeId(storeId).build());
        }

    }

    // 관심 가게 목록 조회
    public List<FavoriteStoresListResponse> getFavoriteStoreList(int memberId) {

        List<FavoriteStoresListResponse> stores = new ArrayList<>();

        List<FavStore> favStores = favStoreRepository.findByMemberId(memberId);

        for (FavStore favStore : favStores) {
            Store store = storeRepository.findById(favStore.getStoreId())
                    .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));
            List<Issue> issues = issueRepository.findMainIssueNameByStoreId(store.getId());
            stores.add(FavoriteStoresListResponse.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .image(store.getImage())
                    .mainMenu(!issues.isEmpty() ?issues.get(0).getName() : null)
                    .build());
        }
        return stores;
    }

    // 사업자 등록
    public void saveBusinessNumber(AddBusinessNumberRequest request, int memberId) {

        businessRegistrationRepository.save(BusinessRegistration.builder()
                .registrationNumber(request.getNumber())
                .registrationAddress(request.getAddress())
                .memberId(memberId)
                .build());

        feignServiceClient.changeRole(RoleRequest.builder().memberId(memberId).role("MANAGER").build());
    }

    // 가게 매출 데이터 분석 조회
    public StoreAnalysisResponse getStoreAnalysis(Integer storeId, StoreAnalysisRequest request, int memberId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        int section2IndexTotal = -1;
        int section2IndexCount = soconRepository.countDistinctUsedIssuedIdByStoreId(storeId, request.getYear(), request.getMonth());
        section2IndexTotal /= 6;
        if(section2IndexCount % 6 != 0){
            section2IndexTotal = section2IndexCount / 6 + 1;
        }
        int section3IndexTotal = -1;
        int section3IndexCount = soconRepository.countDistinctIssuedIdByStoreId(storeId, request.getYear(), request.getMonth());
        if(section3IndexCount % 6 != 0){
            section3IndexTotal = section3IndexCount / 6 + 1;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(request.getYear(),request.getMonth()-1,1);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        IndexRequest indexRequest = IndexRequest.builder()
                .year(request.getYear())
                .month(request.getMonth())
                .index(1)
                .build();



        // section1, section 2
        List<SalesAnalysisResponse> salesAnalysisResponses = getSalesAnalysis(storeId, indexRequest, memberId);
        List<SalesAnalysisResponse> section1 = new ArrayList<>(salesAnalysisResponses.subList(0, 3));

        int nowTotal = soconRepository.sumMothlyUsedSoconByStoreId(storeId, request.getYear(), request.getMonth());
        int last = nowTotal - (section1.get(0).getPrice() + section1.get(1).getPrice() + section1.get(2).getPrice()) ;

        section1.add(SalesAnalysisResponse.builder()
                .name("기타")
                .price(last)
                .used(0)
                .build());

        // section 3


        List<WeeklyAnalysisResponse> weeklyAnalysisResponses = getWeeklyAnalysis(storeId, WeeklyRequest.builder()
                .year(request.getYear())
                .month(request.getMonth())
                .week(1)
                .build(),
                memberId);

        // section 4
        List<IssuedAnalysisListResponse> issuedAnalysisListResponses = getIssueAnalysis(storeId, indexRequest, memberId);

        return StoreAnalysisResponse.builder()
                .storeName(store.getName())
                .year(request.getYear())
                .month(request.getMonth())
                .nowTotal(nowTotal)
                .lastTotal(soconRepository.sumMothlyUsedSoconByStoreId(storeId, request.getYear(), request.getMonth()-1))
                .opendYear(store.getCreatedAt().getYear())
                .opendMonth(store.getCreatedAt().getMonthValue())
                .section1(section1)
                .section2IndexTotal(section2IndexTotal)
                .section2(salesAnalysisResponses)
                .section3IndexTotal(lastDay <= 28? 4 : 5)
                .section3(weeklyAnalysisResponses)
                .section4IndexTotal(section3IndexTotal)
                .section4(issuedAnalysisListResponses)
                .build();


    }

    // 품목별 매출액
    public List<SalesAnalysisResponse> getSalesAnalysis(
            Integer storeId, IndexRequest request, int memberId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        if(store.getMemberId()!= memberId){
            throw new SoconException(ErrorCode.FORBIDDEN);
        }

        List<Integer> issueIds = issueRepository.findIssueIdsByStoreId(storeId);

        List<SalesAnalysisResponse> response = new ArrayList<>();

        for(int issueId : issueIds){
            int usedCount = soconRepository.countUsedSoconByIssueId(issueId, request.getYear(), request.getMonth());
            Issue issue = issueRepository.findById(issueId)
                    .orElseThrow(() -> new StoreException(StoreErrorCode.ISSUE_NOT_FOUND));

            if(issue.getIsDiscounted()){

                response.add(SalesAnalysisResponse.builder()
                        .name(issue.getName())
                        .price(usedCount * issue.getDiscountedPrice())
                                .used(usedCount)
                        .build());
            }else{
                response.add(SalesAnalysisResponse.builder()
                        .name(issue.getName())
                        .price(usedCount * issue.getPrice())
                        .used(usedCount)
                        .build());
            }
        }
        // 정렬
        Comparator<SalesAnalysisResponse> priceComparator = (response1, response2) -> Double.compare(response2.getPrice(), response1.getPrice());

        Collections.sort(response, priceComparator);

        return response.subList((request.getIndex() - 1) * 6, Math.min(request.getIndex() * 6, response.size()));
    }


    // 품목별 발행 현황 리스트 조회
    public List<IssuedAnalysisListResponse> getIssueAnalysis(Integer storeId, IndexRequest request, int memberId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        if(store.getMemberId()!= memberId){
            throw new SoconException(ErrorCode.FORBIDDEN);
        }

        List<Integer> issueIds = issueRepository.findIssueIdsByStoreId(storeId);

        List<IssuedAnalysisListResponse> response = new ArrayList<>();

        for(int issueId : issueIds) {

            int issuedCount = soconRepository.countIssuedSoconByIssueId(issueId, request.getYear(), request.getMonth());
            Issue issue = issueRepository.findById(issueId)
                    .orElseThrow(() -> new StoreException(StoreErrorCode.ISSUE_NOT_FOUND));

            if(issue.getIsDiscounted()){

                response.add(IssuedAnalysisListResponse.builder()
                        .name(issue.getName())
                        .issuePrice(issue.getDiscountedPrice())
                        .totalPrice(issuedCount * issue.getDiscountedPrice())
                        .issued(issuedCount)
                        .build());
            }
            else {
                response.add(IssuedAnalysisListResponse.builder()
                        .name(issue.getName())
                        .issuePrice(issue.getPrice())
                        .totalPrice(issuedCount * issue.getPrice())
                        .issued(issuedCount)
                        .build());
            }
        }
// 정렬
            Comparator<IssuedAnalysisListResponse> priceComparator = (response1, response2) -> Double.compare(response2.getTotalPrice(), response1.getTotalPrice());

            Collections.sort(response, priceComparator);

            return response.subList((request.getIndex() - 1) * 6, Math.min(request.getIndex() * 6, response.size()));

    }

    // 기간별 추이 조회
    public List<WeeklyAnalysisResponse> getWeeklyAnalysis(Integer storeId, WeeklyRequest request, int memberId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        if(store.getMemberId()!= memberId){
            throw new SoconException(ErrorCode.FORBIDDEN);
        }

        int day1 = -1;
        int day2 = -1;
        switch (request.getWeek()){
            case 1:
                day1 = 1;
                day2 = 7;
                break;
            case 2:
                day1 = 8;
                day2 = 15;
                break;
            case 3:
                day1 = 15;
                day2 = 20;
                break;
            case 4:
                day1 = 21;
                day2 = 28;
                break;
            case 5:
                day1 = 29;
                Calendar cal = Calendar.getInstance();
                cal.set(request.getYear(),request.getMonth()-1,1);
                day2 = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                break;
        }
        if(day1 < 0 || day2 < 0){
            throw new SoconException(ErrorCode.BAD_REQUEST);
        }

        List<WeeklyAnalysisResponse> response = new ArrayList<>();

        for(int i = day1; i <= day2; i++){
            int issuedCount = soconRepository.countWeeklyIssuedSoconByIssueId(storeId, request.getYear(), request.getMonth(), i);
            int usedCount = soconRepository.countWeeklyUsedSoconByIssueId(storeId, request.getYear(), request.getMonth(), i);

            response.add(WeeklyAnalysisResponse.builder()
                    .month(request.getMonth())
                    .day(i)
                    .issuedCount(issuedCount)
                    .usedCount(usedCount)
                    .build());
        }


        return response;

    }
}
