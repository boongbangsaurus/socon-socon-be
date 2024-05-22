package site.soconsocon.socon.search.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * "sort": "distance" //최단거리 : distance, 가나다 : name,
 */
@AllArgsConstructor
@Getter
public enum SortType {
    distance,name;
}
