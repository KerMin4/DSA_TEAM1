package com.dsa.team1.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser implements UserDetails {
    
    private static final long serialVersionUID = -2757521351355190L;
    
    String id;
    String password;
    String name;
    String profileImage;  // 프로필 사진 불러오게 추가함 -나연-
    // String roleName;
    // boolean enabled;
    // String username;
    
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    
    @Override
    public String getUsername() {
        return this.id;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    // 1
}


// 아래 원래 코드임 

/* private static final long serialVersionUID = -2757521351355190L;
	
	String id;
	String password;
	String name;
//	String roleName;
//	boolean enabled;
//	String username;
	
	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}

	@Override
	public String getUsername() {
		return this.id;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
	// 1
}
*/