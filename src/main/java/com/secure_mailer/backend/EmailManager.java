package com.secure_mailer.backend;

import com.secure_mailer.backend.service.FetchFolderService;
import com.secure_mailer.backend.service.LoginService;
import com.secure_mailer.frontend.FolderTreeItem;
import com.secure_mailer.frontend.EmailMessage;
import com.secure_mailer.frontend.ViewGenerator;

public class EmailManager {
	
	private static final String LOCATION_OF_DOWNLOADS = System.getProperty("user.home") + "/Downloads/";
	private EmailAccount emailAccount;
	private FolderTreeItem foldersRoot = new FolderTreeItem();
	private FolderTreeItem selectedFolder;
	private EmailMessage selectedMessage;
	
	public void login(String account, String password) {
		emailAccount = new EmailAccount(account, password);
		new LoginService(emailAccount);
	}
	
	public void fetchFolders() {
		FetchFolderService ffs = new FetchFolderService(emailAccount.getStore(), foldersRoot, emailAccount.getFolders());
		ffs.setOnSucceeded(e -> ViewGenerator.showMainWindow(this));
		ffs.start();
	}
	
	public static String getDownloadPath() { return LOCATION_OF_DOWNLOADS; }
	public FolderTreeItem getFolderRoot() { return foldersRoot; }
	public void setSelectedFolder(FolderTreeItem item) { selectedFolder = item; }
	public void setSelectedMessage(EmailMessage emailMessage) { this.selectedMessage = emailMessage; }
	public EmailMessage getSelectedMessage() { return selectedMessage; }	
	public FolderTreeItem getSelectedFolder() { return selectedFolder; }
	public EmailAccount getEmailAccount() { return emailAccount; }	
	
}