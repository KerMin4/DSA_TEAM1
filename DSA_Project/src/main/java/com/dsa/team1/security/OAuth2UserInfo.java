package com.dsa.team1.security;

import java.util.Map;

public interface OAuth2UserInfo {
	Map<String, Object> getAttributes();
	String getProviderId();
	String getProvider();
	String getEmail();
	String getName();
}
