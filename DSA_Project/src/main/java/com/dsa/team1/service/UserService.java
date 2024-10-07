package com.dsa.team1.service;

import java.util.List;

public interface UserService {
	void join(String userName, String password, String phone, String email, String location, String name, String username/*,List<String> interests */);
}
