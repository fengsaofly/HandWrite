package scu.android.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Resource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7683548348315776799L;
	private long resourceId;
	private String resourcePath;

	public Resource(String resourcePath) {
		super();
		this.resourcePath = resourcePath;
	}

	public Resource(long resourceId, String resourcePath) {
		super();
		this.resourceId = resourceId;
		this.resourcePath = resourcePath;
	}

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public int getResouceType() {
		int resourceType = 0;
		if (resourcePath.endsWith(".png") || resourcePath.endsWith(".jpg")) {
			resourceType = 0;
		} else {
			resourceType = 1;
		}
		return resourceType;
	}

	public static String getAudio(ArrayList<Resource> resources) {
		if (resources != null) {
			for (Resource resource : resources) {
				if (resource.getResouceType() == 1) {
					return resource.getResourcePath();
				}
			}
		}
		return null;
	}

	public static ArrayList<String> getImages(ArrayList<Resource> resources) {
		ArrayList<String> images = new ArrayList<String>();
		if (resources != null) {
			for (Resource resource : resources) {
				if (resource.getResouceType() == 0) {
					images.add(resource.getResourcePath());
				}
			}
		}
		return images;
	}

	public String toString() {
		return "|resourceId=" + resourceId + ",resourcePath=" + resourcePath;
	}
}
