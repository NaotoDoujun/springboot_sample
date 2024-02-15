package org.acme.git.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

	private String location = "upload-dir";

	private String remoteurl = "remote-repository-url";

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getRemoteUrl() {
		return remoteurl;
	}

	public void setRemoteUrl(String remoteurl) {
		this.remoteurl = remoteurl;
	}

}
