package site.soconsocon.socon.store.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.soconsocon.socon.store.domain.entity.jpa.Socon;

import java.util.List;

public interface SoconRepository extends JpaRepository<Socon, Integer> {

    @Query("SELECT s FROM SOCON s WHERE s.memberId = :memberId AND (s.status = 'unused' OR s.status = 'sogon') ORDER BY s.expiredAt ASC")
    List<Socon> getUnusedSoconByMemberId(Integer memberId);

    @Query("SELECT s FROM SOCON s WHERE s.memberId = :memberId AND (s.status = 'used' OR s.status = 'expired') ORDER BY FUNCTION('TIMEDIFF', CURRENT_TIMESTAMP, s.usedAt) ASC")
    List<Socon> getUsedSoconByMemberId(Integer memberId);

    @Query("SELECT s FROM SOCON s WHERE s.memberId = :memberId AND s.issue.storeName LIKE %:storeName% AND s.status = 'unused' ORDER BY FUNCTION('TIMEDIFF', CURRENT_TIMESTAMP, s.expiredAt) ASC")
    List<Socon> getSoconByMemberIdAndStoreName(Integer memberId, String storeName);

    @Query("SELECT s FROM SOCON s WHERE s.memberId = :memberId AND s.issue.item.name LIKE %:itemName% AND s.status = 'unused' ORDER BY FUNCTION('TIMEDIFF', CURRENT_TIMESTAMP, s.expiredAt) ASC")
    List<Socon> getSoconByMemberIdAndItemName(Integer memberId, String itemName);

    @Query("SELECT s FROM SOCON s WHERE s.id = :issueId AND (s.status = 'unused' OR s.status = 'sogon')")
    List<Socon> getUnusedSoconByIssueId(Integer issueId);
    @Query("SELECT s FROM SOCON s WHERE s.issue.item.store.id = :storeId AND s.status  = 'unused' OR s.status = 'sogon'")
    List<Socon> getSoconByStoreId(Integer storeId);

    @Query("SELECT COUNT(s) FROM SOCON s WHERE s.issue.id = :issueId AND (s.status = 'used') AND YEAR(s.usedAt) = :year AND MONTH(s.usedAt) = :month")
    int countUsedSoconByIssueId(Integer issueId, int year, int month);

    @Query("SELECT COUNT(s) FROM SOCON s WHERE s.issue.id = :issueId AND YEAR(s.purchasedAt) = :year AND MONTH(s.purchasedAt) = :month")
    int countIssuedSoconByIssueId(int issueId, int year, int month);

    @Query("SELECT COUNT(s) FROM SOCON s WHERE s.issue.item.store.id = :storeId AND YEAR(s.purchasedAt) = :year AND MONTH(s.purchasedAt) = :month AND DAY(s.purchasedAt) = :day")
    int countWeeklyIssuedSoconByIssueId(int storeId, int year, int month, int day);

    @Query("SELECT COUNT(s) FROM SOCON s WHERE s.issue.item.store.id = :storeId AND YEAR(s.usedAt) = :year AND MONTH(s.usedAt) = :month AND DAY(s.usedAt) = :day")
    int countWeeklyUsedSoconByIssueId(int storeId, int year, int month, int day);


    @Query("SELECT SUM(CASE WHEN s.issue.isDiscounted = true THEN s.issue.price ELSE s.issue.discountedPrice END)\n" +
            "FROM SOCON s \n" +
            "WHERE s.issue.item.store.id = :storeId \n" +
            "AND YEAR(s.usedAt) = :year \n" +
            "AND MONTH(s.usedAt) = :month")
    int sumMothlyUsedSoconByStoreId(int storeId, int year, int month);
        @Query("SELECT COUNT(DISTINCT s.issue.id) FROM SOCON s WHERE s.issue.item.store.id = :storeId AND YEAR(s.usedAt) = :year AND MONTH(s.usedAt) = :month")
    int countDistinctUsedIssuedIdByStoreId(int storeId, int year, int month);

    @Query("SELECT COUNT(DISTINCT s.issue.id) FROM SOCON s WHERE s.issue.item.store.id = :storeId AND YEAR(s.purchasedAt) = :year AND MONTH(s.purchasedAt) = :month")
    int countDistinctIssuedIdByStoreId(int storeId, int year, int month);

    @Query("SELECT COUNT(s) FROM SOCON s WHERE s.memberId = :memberId AND s.status='unused'")
    int getMySoconCount(Integer memberId);
}
