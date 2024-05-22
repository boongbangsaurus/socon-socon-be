package site.soconsocon.socon.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soconsocon.socon.search.domain.dto.request.SearchRequest;
import site.soconsocon.socon.search.domain.dto.request.StoreCreateDocument;
import site.soconsocon.socon.search.domain.dto.request.StoreNearMe;
import site.soconsocon.socon.search.domain.dto.response.FoundStoreInfo;
import site.soconsocon.socon.search.service.SearchService;
import site.soconsocon.socon.store.service.SoconRedisService;
import site.soconsocon.utils.MessageUtils;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final SoconRedisService soconRedisService;
    @PostMapping(value = "/detail", produces = "application/json; charset=UTF-8")
    public ResponseEntity getStoresDetail(
            @RequestBody SearchRequest searchRequest,
            @RequestHeader("X-Authorization-Id") int memberId){
        List<FoundStoreInfo> storeInfoList = searchService.searchStores(searchRequest, memberId);
        return ResponseEntity.ok().body(MessageUtils.success(storeInfoList));
    }

    @PostMapping("/me")
    public ResponseEntity sendNotification(
            @RequestBody StoreNearMe storeNearMe,
            @RequestHeader("X-Authorization-Id") int memberId){
        soconRedisService.findAndNotify(storeNearMe, memberId);
        return ResponseEntity.ok().body(MessageUtils.success());
    }

    @PostMapping("/document")
    public ResponseEntity addStoreDocument(
            @RequestBody StoreCreateDocument storeCreateDocument){
        searchService.addStores(storeCreateDocument);
        return ResponseEntity.ok().body(MessageUtils.success());
    }

    @GetMapping("/dump")
    public ResponseEntity dumpStoreDocument(){
        searchService.dumpStores();
        return ResponseEntity.ok().body(MessageUtils.success());
    }


}
