package site.soconsocon.auth.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import site.soconsocon.auth.domain.entity.jpa.RefreshToken;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {

    RefreshToken findRefreshTokenByMemberId(int memberId);

    RefreshToken findByMemberIdAndRefreshToken(int memberId, String refreshToken);

    RefreshToken findByRefreshToken(String refreshToken);

    void deleteRefreshTokenByMemberId(int memberId);
}
