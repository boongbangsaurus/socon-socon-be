package site.soconsocon.socon.search.domain.document;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.time.LocalDate;

@Getter
@Setter
@Document(indexName = "stores")
@Builder
public class StoreDocument {
    @Id
    private Integer id;

    // 'nori' 분석기는 'text' 타입 필드에만 적용되어야 합니다.
    @Field(type = FieldType.Text, analyzer = "nori", fielddata = true)
    private String name;

    @Field(type = FieldType.Keyword) // 'category'는 키워드로 충분합니다.
    private String category; // 가게 분류

    private String image; // 가게 대표 이미지

    @GeoPointField
    private GeoPoint location;

    @Field(type = FieldType.Keyword)
    private String address; // 가게 주소

    @Field(type = FieldType.Text, analyzer = "nori")
    private String introduction; // 가게 설명

    private LocalDate createdAt;// 등록일

}
