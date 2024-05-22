package site.soconsocon.socon.search.domain.dto.request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.geo.Point;
import site.soconsocon.socon.search.domain.document.StoreDocument;
import site.soconsocon.socon.store.domain.entity.jpa.BusinessHour;
import site.soconsocon.socon.store.domain.entity.jpa.BusinessRegistration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StoreCreateDocument {
    private Integer id;
    private String name;
    private String category; // 가게 분류
    private String image; // 가게 대표 이미지
    private String phoneNumber; // 가게 전화번호
    private Double lat; // 위도
    private Double lng; // 경도
    private String address; // 가게 주소
    private String introduction; // 가게 설명
    public StoreDocument toDocument(){
        return StoreDocument.builder()
                .id(id)
                .name(name)
                .category(category)
                .image(image)
                .location(new GeoPoint(lat, lng))
                .address(address)
                .introduction(introduction)
                .build();
    }
}
