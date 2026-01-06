package com.shop.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.shop.domain.user.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultCurrentUser implements UserDetails, CurrentUser, Serializable {
	private static final long serialVersionUID = 1L;
	//jwt파싱해서 넣어주면 될 것 같음
	private Long id;
	private String loginId;
	private Role role;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getLoginId() {
		return loginId;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public String getUsername() {
		return id.toString();
	}

	@Override
	public Role getRole() {
		return this.role;
	}
}
