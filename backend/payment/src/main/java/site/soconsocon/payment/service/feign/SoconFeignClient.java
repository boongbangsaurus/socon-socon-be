package site.soconsocon.payment.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import site.soconsocon.payment.service.feign.request.AddMySoconRequest;

@FeignClient(name = "socon-service", url = "${feign.urls.issues}")
public interface SoconFeignClient {

    //소콘북 저장
    @PostMapping(value = "/socon", produces = "application/json", consumes = "application/json")
    ResponseEntity<Object> saveMySocon(@RequestBody AddMySoconRequest addMySoconRequest);
}
