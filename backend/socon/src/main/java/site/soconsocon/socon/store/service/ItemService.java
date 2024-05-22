package site.soconsocon.socon.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.soconsocon.socon.global.domain.ErrorCode;
import site.soconsocon.socon.global.exception.SoconException;
import site.soconsocon.socon.store.domain.dto.request.AddItemRequest;
import site.soconsocon.socon.store.domain.dto.response.ItemListResponse;
import site.soconsocon.socon.store.domain.dto.response.ItemResponse;
import site.soconsocon.socon.store.domain.entity.jpa.Item;
import site.soconsocon.socon.store.domain.entity.jpa.Store;
import site.soconsocon.socon.store.exception.StoreErrorCode;
import site.soconsocon.socon.store.exception.StoreException;
import site.soconsocon.socon.store.repository.jpa.ItemRepository;
import site.soconsocon.socon.store.repository.jpa.StoreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ItemService {
    private final StoreRepository storeRepository;
    private final ItemRepository itemRepository;

    // 상품 정보 등록
    public void saveItem(AddItemRequest request, Integer storeId, int memberId) {

        Store savedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        if (!Objects.equals(savedStore.getMemberId(), memberId)) {
            // 점포 소유주 불일치
            throw new SoconException(ErrorCode.FORBIDDEN);
        }
        itemRepository.save(Item.builder()
                .name(request.getName())
                .image(request.getImage())
                .price(request.getPrice())
                .summary(request.getSummary())
                .description(request.getDescription())
                .store(savedStore)
                .build());
    }

    // 점주 가게 상품 목록 조회
    public List<ItemListResponse> getItemList(Integer StoreId, int memberId) {

        if (!Objects.equals(storeRepository.findMemberIdByStoreId(StoreId), memberId)) {
            // 점포 소유주 불일치
            throw new SoconException(ErrorCode.FORBIDDEN);
        }
        List<Item> itemList = itemRepository.findItemsByStoreId(StoreId);
        List<ItemListResponse> itemResponseList = new ArrayList<>();
        for(Item item : itemList) {
            itemResponseList.add(ItemListResponse.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .image(item.getImage())
                    .price(item.getPrice())
                    .build());
        }

        return itemResponseList;
    }

    // 상품 정보 상세 조회
    public ItemResponse getDetailItemInfo(Integer storeId, Integer itemId, int memberId) {

        if (!Objects.equals(memberId, storeRepository.findMemberIdByStoreId(storeId))) {
            throw new SoconException(ErrorCode.FORBIDDEN);
        }

        Item item = itemRepository.findItemResponseByItemId(itemId);

        if(item == null) {
            throw new StoreException(StoreErrorCode.ITEM_NOT_FOUND);
        }
        else{
            return ItemResponse.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .itemImage(item.getImage())
                    .storeImage(item.getStore().getImage())
                    .price(item.getPrice())
                    .summary(item.getSummary())
                    .description(item.getDescription())
                    .build();
        }
    }
}
