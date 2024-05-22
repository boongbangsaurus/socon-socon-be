package site.soconsocon.socon.search.repository.elasticsearch;

import co.elastic.clients.elasticsearch._types.*;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Repository;
import site.soconsocon.socon.search.domain.document.StoreDocument;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class CustomSearchRepositoryImpl implements CustomSearchRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    public CustomSearchRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public Page<StoreDocument> findStoreDocumentsByLocationNear(Point point, Pageable pageable,String sort) {
        GeoLocation geoLocation = new GeoLocation.Builder()
                .coords(Arrays.asList(point.getX(),point.getY()))
                .build();

        GeoDistanceSort geoDistanceSort = new GeoDistanceSort.Builder()
                .field("location")
                .location(geoLocation)
                .order(SortOrder.Asc).build();

        // 이름 순 정렬
        FieldSort fieldSort = new FieldSort.Builder()
                .field("name")
                .order(SortOrder.Asc)
                .build();

        SortOptions geoSortOptions = new SortOptions.Builder().geoDistance(geoDistanceSort).build();
        SortOptions fieldSortOptions = new SortOptions.Builder().field(fieldSort).build();

        NativeQuery searchQuery = NativeQuery.builder()
                .withFilter(new Query.Builder()
                        .geoDistance(new GeoDistanceQuery.Builder()
                                .field("location")
                                .location(geoLocation)
                                .distance("15km")
                                .build())
                        .build())
                .withSort(Arrays.asList(geoSortOptions,fieldSortOptions))
                .build();

        // 검색
        SearchHits<StoreDocument> searchHits = elasticsearchOperations.search(searchQuery, StoreDocument.class);
        List<StoreDocument> storeDocuments = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        return new PageImpl<>(storeDocuments, pageable, searchHits.getTotalHits());
    }


    @Override
    public Page<StoreDocument> findStoreDocumentsByLocationNearAndContent(Point point, String type, String content, Pageable pageable, String sort) {
        GeoLocation geoLocation = new GeoLocation.Builder()
                .coords(Arrays.asList(point.getX(),point.getY()))
                .build();

        GeoDistanceSort geoDistanceSort = new GeoDistanceSort.Builder()
                .field("location")
                .location(geoLocation)
                .order(SortOrder.Asc).build();

        // 이름 순 정렬
        FieldSort fieldSort = new FieldSort.Builder()
                .field("name")
                .order(SortOrder.Asc)
                .build();

        // 정렬 옵션 지정
        SortOptions sortOptions = null;

        if(sort.equals("distance")){
            sortOptions = new SortOptions.Builder().geoDistance(geoDistanceSort).build();
        }else if(sort.equals("name")){
            sortOptions = new SortOptions.Builder().field(fieldSort).build();
        }
        NativeQuery searchQuery = NativeQuery.builder()
                .withFilter(new Query.Builder()
                        .geoDistance(new GeoDistanceQuery.Builder()
                                .field("location")
                                .location(geoLocation)
                                .distance("15km")
                                .build())
                        .build())
                .withQuery(new Query.Builder()
                        .queryString(new QueryStringQuery.Builder()
                                .defaultField(type)
                                .query("*" + content + "*")
                                .build()
                        )
                        .build())
                .withSort(Arrays.asList(sortOptions))
                .build();

        // 검색
        SearchHits<StoreDocument> searchHits = elasticsearchOperations.search(searchQuery, StoreDocument.class);
        List<StoreDocument> storeDocuments = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
        return new PageImpl<>(storeDocuments, pageable, searchHits.getTotalHits());
    }
}
