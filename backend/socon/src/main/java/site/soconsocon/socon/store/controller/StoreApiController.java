package site.soconsocon.socon.store.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soconsocon.socon.store.domain.dto.request.*;
import site.soconsocon.socon.store.domain.dto.response.IssueListResponse;
import site.soconsocon.socon.store.domain.dto.response.ItemListResponse;
import site.soconsocon.socon.store.domain.dto.response.SalesAnalysisResponse;
import site.soconsocon.socon.store.domain.dto.response.StoreInfoResponse;
import site.soconsocon.socon.store.service.IssueService;
import site.soconsocon.socon.store.service.ItemService;
import site.soconsocon.socon.store.service.StoreService;
import site.soconsocon.utils.MessageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Slf4j
public class StoreApiController {

    private final StoreService storeService;
    private final IssueService issueService;
    private final ItemService itemService;

    // 가게 정보 등록
    @PostMapping(value = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> saveStore(
            @Valid
            @RequestBody
            AddStoreRequest request,
            @RequestHeader("X-Authorization-Id") int memberId
    ) {

        storeService.saveStore(request, memberId);

        return ResponseEntity.ok().body(MessageUtils.success(null));
    }

    // 점주 등록 가게 정보 목록 조회
    @GetMapping(value = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> getStoreList(@RequestHeader("X-Authorization-Id") int memberId) {

        return ResponseEntity.ok().body(MessageUtils.success(storeService.getStoreList(memberId)));
    }

    // 가게 정보 상세 조회
    @GetMapping(value = "/{store_id}/info", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> getStoreInfo(
            @PathVariable("store_id") Integer storeId
    ) {
        StoreInfoResponse store = storeService.getStoreInfo(storeId);
        List<IssueListResponse> issues = issueService.getIssueList(storeId);
        Map<String, Object> response = new HashMap<>();

        response.put("store", store);
        response.put("issues", issues);

        return ResponseEntity.ok().body(MessageUtils.success(response));
    }

    // 점주 가게 상세 정보 조회`
    @GetMapping(value = "/{store_id}/manage/info", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> getDetailStoreInfo(
            @PathVariable("store_id") Integer storeId,
            @RequestHeader("X-Authorization-Id") int memberId
    ) {
        List<ItemListResponse> items = itemService.getItemList(storeId, memberId);
        List<IssueListResponse> issues = issueService.getIssueList(storeId);

        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("issues", issues);

        return ResponseEntity.ok().body(MessageUtils.success(response));
    }

    // 가게 정보 수정
    @PutMapping(value = "/{store_id}/info", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> updateStoreInfo(
            @RequestBody UpdateStoreInfoRequest request,
            @PathVariable("store_id") Integer storeId,
            @RequestHeader("X-Authorization-Id") int memberId
    ) {

        storeService.updateStoreInfo(storeId, request, memberId);

        return ResponseEntity.ok().body(MessageUtils.success(null));
    }

    // 가게 폐업 정보 업데이트
    @PutMapping(value = "/{store_id}/manage/info", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> updateClosedPlanned(
            @PathVariable("store_id") Integer storeId,
            @RequestBody UpdateClosedPlannedRequest request,
            @RequestHeader("X-Authorization-Id") int memberId
    ) {
        storeService.updateClosedPlanned(storeId, request, memberId);

        return ResponseEntity.ok().body(MessageUtils.success());
    }

    // 상품 정보 등록
    @PostMapping(value ="/{store_id}/items", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> saveStoreItem(
            @PathVariable("store_id") Integer storeId,
            @RequestBody AddItemRequest request,
            @RequestHeader("X-Authorization-Id") int memberId
    ) {

        itemService.saveItem(request, storeId, memberId);

        return ResponseEntity.ok().body(MessageUtils.success(null, "201 CREATED", null));
    }

    // 상품 정보 상세 조회
    @GetMapping(value = "/{store_id}/items/{item_id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> getDetailItemInfo(
            @PathVariable("store_id") Integer storeId,
            @PathVariable("item_id") Integer itemId,
            @RequestHeader("X-Authorization-Id") int memberId
    ) {
        return ResponseEntity.ok().body(MessageUtils.success(itemService.getDetailItemInfo(storeId, itemId, memberId)));
    }

    // 상품 발행 정보 등록
    @PostMapping(value = "/{store_id}/items/{item_id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> saveIssue(
            @PathVariable("store_id") Integer storeId,
            @PathVariable("item_id") Integer itemId,
            @RequestBody AddIssueRequest request,
            @RequestHeader("X-Authorization-Id") int memberId
    ) {
        issueService.saveIssue(request, storeId, itemId, memberId);

        return ResponseEntity.ok().body(MessageUtils.success(null, "201 CREATED", null));
    }

    // 관심 가게 추가, 취소
    @PostMapping(value = "/favorite/{store_id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> favoriteStore(
            @PathVariable("store_id") Integer storeId,
            @RequestHeader("X-Authorization-Id") int memberId
    ) {
        storeService.favoriteStore(storeId, memberId);

        return ResponseEntity.ok().body(MessageUtils.success(null, "204 NO CONTENT", null));
    }

    // 관심 가게 목록 조회
    @GetMapping(value = "/favorite" , produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> getFavoriteList(
            @RequestHeader("X-Authorization-Id") int memberId
    ) {

        return ResponseEntity.ok().body(MessageUtils.success(storeService.getFavoriteStoreList(memberId)));
    }

    // 사업자 번호 등록
    @PostMapping(value = "/business" , produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> saveBusinessNumber(
            @Valid
            @RequestBody
            AddBusinessNumberRequest request,
            @RequestHeader("X-Authorization-Id") int memberId
    ){

        storeService.saveBusinessNumber(request, memberId);

        return ResponseEntity.ok().body(MessageUtils.success(null, "201 CREATED", null));
    }

    // 사업자 번호 목록 조회
    @GetMapping(value = "/business", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> getBusinessNumberList(
            @RequestHeader("X-Authorization-Id") int memberId
    ){
        return ResponseEntity.ok().body(MessageUtils.success(storeService.getBusinessNumberList(memberId)));
    }

    // 가게 매출 데이터 분석 조회
    @PostMapping(value = "/{store_id}/analysis", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> getStoreAnalysis(
            @PathVariable("store_id") Integer storeId,
            @RequestBody StoreAnalysisRequest request,
            @RequestHeader("X-Authorization-Id") int memberId
    ){

        return ResponseEntity.ok().body(MessageUtils.success(storeService.getStoreAnalysis(storeId, request, memberId)));
    }

    // 품목별 매출액 리스트 조회
    @PostMapping(value = "/{store_id}/analysis/sales", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> getSalesAnalysis(
            @PathVariable("store_id") Integer storeId,
            @RequestBody IndexRequest request,
            @RequestHeader("X-Authorization-Id") int memberId
    ){

        List<SalesAnalysisResponse> response = storeService.getSalesAnalysis(storeId, request, memberId);



        return ResponseEntity.ok().body(MessageUtils.success(response));
    }


    // 기간별 추이 리스트 조회
    @PostMapping(value = "/{store_id}/analysis/weekly", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> getWeeklyAnalysis(
            @PathVariable("store_id") Integer storeId,
            @RequestBody WeeklyRequest request,
            @RequestHeader("X-Authorization-Id") int memberId
    ){

        return ResponseEntity.ok().body(MessageUtils.success(storeService.getWeeklyAnalysis(storeId, request, memberId)));
    }


// 품목별 발행 현황 리스트 조회
    @PostMapping(value = "/{store_id}/analysis/issues", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> getIssueAnalysis(
            @PathVariable("store_id") Integer storeId,
            @RequestBody IndexRequest request,
            @RequestHeader("X-Authorization-Id") int memberId
    ){

        return ResponseEntity.ok().body(MessageUtils.success(storeService.getIssueAnalysis(storeId, request, memberId)));
    }

}
