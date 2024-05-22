package site.soconsocon.socon.search.repository.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import site.soconsocon.socon.search.domain.document.StoreDocument;

import java.util.Optional;

public interface SearchRepository extends ElasticsearchRepository<StoreDocument, Integer>, CustomSearchRepository {
}
