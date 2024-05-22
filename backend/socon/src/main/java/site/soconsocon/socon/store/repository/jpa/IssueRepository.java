package site.soconsocon.socon.store.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.soconsocon.socon.store.domain.entity.jpa.Issue;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Integer> {

    @Query("SELECT i FROM ISSUE i WHERE i.item.store.id = :storeId")
    List<Issue> findIssueListByStoreId(Integer storeId);

    @Query("SELECT i FROM ISSUE i WHERE i.item.store.id = :storeId AND i.isMain = true AND i.status = 'A'")
    List<Issue> findMainIssueNameByStoreId(Integer storeId);

    @Query("SELECT i.item.store.memberId FROM ISSUE i WHERE i.id = :issueId")
    Integer findMemberIdByIssueId(Integer issueId);


    @Query("SELECT i FROM ISSUE i WHERE i.item.store.id = :storeId AND i.status = 'A'")
    List<Issue> findActiveIssuesByStoreId(Integer storeId);


    @Query("SELECT i.id FROM ISSUE i WHERE i.item.store.id = :storeId")
    List<Integer> findIssueIdsByStoreId(Integer storeId);
}
