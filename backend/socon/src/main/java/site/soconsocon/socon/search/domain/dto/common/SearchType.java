package site.soconsocon.socon.search.domain.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * "name", //상호명 : name, 카테고리 : category,  도로명 주소 : address
 */
@AllArgsConstructor
@Getter
public enum SearchType {
    name,category,address;
}
