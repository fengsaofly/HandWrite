package scu.android.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Resource implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7683548348315776799L;
	private long resourceId;
	private String resourceSPath;
	private String resourceLPath;

	public Resource(long resourceId, String resourceSPath, String resourceLPath) {
		super();
		this.resourceId = resourceId;
		this.resourceSPath = resourceSPath;
		this.resourceLPath = resourceLPath;
	}

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceSPath() {
		return resourceSPath;
	}

	public void setResourceSPath(String resourceSPath) {
		this.resourceSPath = resourceSPath;
	}

	public String getResourceLPath() {
		return resourceLPath;
	}

	public void setResourceLPath(String resourceLPath) {
		this.resourceLPath = resourceLPath;
	}

	/**
	 * get resource type
	 * 
	 * @param path
	 *            resource path or name
	 * 
	 * @return 0:image,1:audio,2:unknown
	 */
	public static int getType(final String path) {
		if (path.endsWith(".jpg") || path.endsWith(".JPG")
				|| path.endsWith(".png") || path.endsWith(".PNG")) {
			return 0;
		} else if (path.endsWith(".amr") || path.endsWith(".AMR")) {
			return 1;
		}
		return 2;
	}

	public static String getAudio(ArrayList<Resource> resources) {
		if (resources != null) {
			for (Resource resource : resources) {
				if (getType(resource.getResourceLPath()) == 1) {
					return resource.getResourceLPath();
				}
			}
		}
		return null;
	}

	public static ArrayList<String> getLImages(ArrayList<Resource> resources) {
		ArrayList<String> images = new ArrayList<String>();
		if (resources != null) {
			for (Resource resource : resources) {
				if (getType(resource.getResourceLPath()) == 0) {
					images.add(resource.getResourceLPath());
				}
			}
		}
		return images;
	}

	public static ArrayList<String> getSImages(ArrayList<Resource> resources) {
		ArrayList<String> images = new ArrayList<String>();
		if (resources != null) {
			for (Resource resource : resources) {
				if (getType(resource.getResourceSPath()) == 0) {
					images.add(resource.getResourceSPath());
				}
			}
		}
		return images;
	}

	public String toString() {
		return "|resourceId=" + resourceId + ",resourceSPath=" + resourceSPath
				+ ",resourceLPath=" + resourceLPath;
	}
}
