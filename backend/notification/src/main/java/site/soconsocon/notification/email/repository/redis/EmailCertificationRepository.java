package site.soconsocon.notification.email.repository.redis;

import org.springframework.data.repository.CrudRepository;
import site.soconsocon.notification.email.domain.entity.CertificationNumber;

public interface EmailCertificationRepository extends CrudRepository<CertificationNumber, String> {

}