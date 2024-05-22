package site.soconsocon.socon.search.repository.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import site.soconsocon.socon.search.domain.document.StoreDocument;

public interface CustomSearchRepository {
    Page<StoreDocument> findStoreDocumentsByLocationNear(Point point, Pageable pageable, String sort);
    Page<StoreDocument> findStoreDocumentsByLocationNearAndContent(Point point,String type,String content, Pageable pageable, String sort);
}

