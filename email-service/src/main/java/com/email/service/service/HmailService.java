package com.email.service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

@Service
public class HmailService {
	
	@Value("${hmail.admin.username}")
	private String hmailAdminUsername;
	
	@Value("${hmail.admin.password}")
	private String hmailAdminPassword;
	
	public void createMailBox(String email,String rawPassword) {
		// TODO Auto-generated method stub
		String domainName = email.substring(email.indexOf("@")+1);
		
		ActiveXComponent hMailAppServer = null;
		
		try {
			hMailAppServer = new ActiveXComponent("hMailServer.Application");
			Dispatch appDispatch = hMailAppServer.getObject();
			
			Dispatch.call(appDispatch,"Authenticate",new Variant(hmailAdminUsername),new Variant(hmailAdminPassword));
		
			Dispatch domains = Dispatch.get(appDispatch,"Domains").toDispatch();
			
			Dispatch domain = Dispatch.call(domains,"ItemByName",domainName).toDispatch();
			
			Dispatch accounts = Dispatch.get(domain,"Accounts").toDispatch();
			
			Dispatch account = Dispatch.call(accounts,"Add").toDispatch();
			
			Dispatch.put(account,"Address",email);
			Dispatch.put(account,"Password",rawPassword);
			Dispatch.put(account,"Active",true);
			
			Dispatch.call(account, "Save");
			
			
		}catch(Exception e) {
			throw new RuntimeException("Error creating hMailServer Account");
			
		}finally {
			if(hMailAppServer != null) {
				hMailAppServer.safeRelease();
			}
		}
		
		
	}

}
