package site.soconsocon.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.soconsocon.auth.domain.entity.jpa.Qna;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Integer> {
}
