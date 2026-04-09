package com.email.service.dto;


public class UserSmtpConfig {
	
	
	private Long id;
	
	
	private String userEmail;
	
	private String smtpHost;
	
	private int smtpPort;
	
	private String smtpUsername;
	
	private String encryptedPassword;
	
	private boolean tlsEnabled;
	
	private boolean authEnabled;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpUsername() {
		return smtpUsername;
	}

	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public boolean isTlsEnabled() {
		return tlsEnabled;
	}

	public void setTlsEnabled(boolean tlsEnabled) {
		this.tlsEnabled = tlsEnabled;
	}

	public boolean isAuthEnabled() {
		return authEnabled;
	}

	public void setAuthEnabled(boolean authEnabled) {
		this.authEnabled = authEnabled;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	
	
	

}
