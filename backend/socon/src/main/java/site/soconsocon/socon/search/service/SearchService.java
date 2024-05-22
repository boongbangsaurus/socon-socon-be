package site.soconsocon.socon.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import site.soconsocon.socon.global.GeoUtils;
import site.soconsocon.socon.search.domain.document.StoreDocument;
import site.soconsocon.socon.search.domain.dto.common.SearchType;
import site.soconsocon.socon.search.domain.dto.request.SearchRequest;
import site.soconsocon.socon.search.domain.dto.request.StoreCreateDocument;
import site.soconsocon.socon.search.domain.dto.request.StoreNearMe;
import site.soconsocon.socon.search.domain.dto.response.FoundStoreInfo;
import site.soconsocon.socon.search.exception.SearchErrorCode;
import site.soconsocon.socon.search.exception.SearchException;
import site.soconsocon.socon.search.repository.elasticsearch.SearchRepository;
import site.soconsocon.socon.store.domain.entity.jpa.FavStore;
import site.soconsocon.socon.store.domain.entity.jpa.Store;
import site.soconsocon.socon.store.domain.entity.redis.IssueRedis;
import site.soconsocon.socon.store.repository.jpa.FavStoreRepository;
import site.soconsocon.socon.store.repository.jpa.StoreRepository;
import site.soconsocon.socon.store.repository.redis.IssueRedisRepository;
import site.soconsocon.socon.store.service.SoconRedisService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {
    private final SearchRepository searchRepository;
    private final FavStoreRepository favStoreRepository;
    private final IssueRedisRepository issueRedisRepository;
    public ArrayList<FoundStoreInfo> searchStores(SearchRequest searchRequest, Integer memberId){
        ArrayList<FoundStoreInfo> foundStoreInfoList = new ArrayList<>();
        log.warn(searchRequest.toString());
        log.warn("memberId : "+memberId);
        Point location = new Point(searchRequest.getLng(), searchRequest.getLat());
        Distance distance = new Distance(3);

        // default pagination value
        int page = searchRequest.getPage() == null ? 0 : searchRequest.getPage(); // Page number, 0-based
        int size = searchRequest.getSize() == null ? 0 : searchRequest.getSize(); // Number of items per page

        // Define sorting method
        Pageable pageable = pageable = PageRequest.of(page, size);
        log.info(searchRequest.toString());
        Page<StoreDocument> foundStores = null;
        List<FavStore> favStoreList =null;
        Set<Integer> favStoreIdList = null;
        try {
            // create set for field isLike
            favStoreList = favStoreRepository.findByMemberId(memberId);
            favStoreIdList = favStoreList != null ? favStoreList.stream()
                    .map(FavStore::getStoreId)
                    .collect(Collectors.toSet()) : Collections.emptySet();
        } catch (RuntimeException e){
            log.warn(e.getStackTrace().toString());
            throw new SearchException(SearchErrorCode.SEARCH_FAIL);
        }
        try {
            if(searchRequest.getSearchType().equals(SearchType.address)){
                log.info(searchRequest.getSearchType().name());
                foundStores = searchRepository.findStoreDocumentsByLocationNear(location, pageable,searchRequest.getSort());
            }
            else if(searchRequest.getSearchType().equals(SearchType.category) ||searchRequest.getSearchType().equals(SearchType.name)){
                log.info(searchRequest.getSearchType().name());
                foundStores = searchRepository.findStoreDocumentsByLocationNearAndContent(location, searchRequest.getSearchType().name(), searchRequest.getContent(), pageable,searchRequest.getSort());
            }else {
                log.error(SearchType.address.toString());
                throw new SearchException(SearchErrorCode.INVALID_FORMAT);
            }
        }catch (RuntimeException e){
            throw new SearchException(SearchErrorCode.SEARCH_FAIL);
        }
        // generate FoundStoreInfo DTO
        for (StoreDocument storeDocument:foundStores) {
            boolean isLike = favStoreIdList.contains(storeDocument.getId());
            List<IssueRedis> issue = null;
            try {
                issue = issueRedisRepository.findByStoreId(storeDocument.getId());
            }catch (RuntimeException e){
                throw new SearchException(SearchErrorCode.SEARCH_FAIL);
            }
            int calculatedDistance = (int) Math.ceil(
                    GeoUtils.distance(
                            searchRequest.getLat(),
                            searchRequest.getLng(),
                            storeDocument.getLocation().getLat(),
                            storeDocument.getLocation().getLon()
                    )
            );

            FoundStoreInfo storeInfo = FoundStoreInfo.builder()
                    .storeId(storeDocument.getId()) // 예를 들어 기본값으로 0 사용
                    .name(storeDocument.getName() != null ? storeDocument.getName() : "")
                    .imageUrl(storeDocument.getImage() != null ? storeDocument.getImage() : "")
                    .address(storeDocument.getAddress() != null ? storeDocument.getAddress() : "")
                    .category(storeDocument.getCategory() != null ? storeDocument.getCategory() : "")
                    .isLike(isLike) // Boolean은 null이 가능한 상황에서 기본값이 false인 경우를 다룰 수 있음
                    .mainSocon(issue != null && !issue.isEmpty() ? issue.get(0).getName() : "")
                    .distance(calculatedDistance)
                    .build();
            // filter favourites
            if(searchRequest.getIsFavoriteSearch()){
                if(isLike) foundStoreInfoList.add(storeInfo);
            }else {
                foundStoreInfoList.add(storeInfo);
            }
        }
        return foundStoreInfoList;
    }

    public void addStores(StoreCreateDocument storeCreateDocument) {
        try{
            searchRepository.save(storeCreateDocument.toDocument());
        } catch (RuntimeException e){
            throw new SearchException(SearchErrorCode.SAVE_DOCUMENT_FAIL);
        }
    }

    private final StoreRepository storeRepository;
    public void dumpStores() {
        try {
            List<Store> stores = storeRepository.findAll();
            for(Store store : stores){
                searchRepository.save(StoreDocument.builder()
                        .id(store.getId())
                        .image(store.getImage())
                        .introduction(store.getIntroduction())
                        .name(store.getName())
                        .category(store.getCategory())
                        .location(new GeoPoint(store.getLat(), store.getLng()))
                        .build());
            }
        }catch (RuntimeException e){
            throw new SearchException(SearchErrorCode.SEARCH_FAIL);
        }

    }
}
