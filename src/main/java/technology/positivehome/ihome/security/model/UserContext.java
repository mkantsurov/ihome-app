package technology.positivehome.ihome.security.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.*;


public record UserContext(long userId, String clientIp, List<GrantedAuthority> authorities) {

    public static Builder builder(long userId) {
        if (userId == 0) {
            throw new IllegalArgumentException("UserId :" + userId);
        }
        return new Builder(userId);
    }

    public static class Builder {
        private final long userId;
        private String clientIp;
        private final List<GrantedAuthority> authorities = new ArrayList<>();

        public Builder(long userId) {
            this.userId = userId;
        }

        public Builder withClientIp(String clientIp) {
            this.clientIp = clientIp;
            return this;
        }

        public Builder withAuthorities(Collection<GrantedAuthority> authorities) {
            Set<GrantedAuthority> authoritySet = new HashSet<>(this.authorities);
            authoritySet.addAll(authorities);
            this.authorities.clear();
            this.authorities.addAll(authoritySet);
            return this;
        }

        public UserContext build() {
            return new UserContext(userId, clientIp, authorities);
        }
    }
}
