/**
 *
 */
package net.meiteampower.blog.blog_util;

import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author kie
 */
public class FileInfo implements Serializable {

	private String path;
	private int depth;

	public FileInfo(String root, String path) throws IOException {

		if (!path.startsWith(root)) {
			throw new IOException("Illigal path " + path);
		}
		this.path = path;

		String relativePath = path.replace(root, "");
		int count = 0;
		int index = 0;
		while ((index = relativePath.indexOf('\\', index)) >= 0) {
			count++;
			index++;
		}
		depth = count;

//		System.out.println(this.getDepth() + " - " + this.getPath());
	}

	public final String getPath() {
		return path;
	}

	public final void setPath(String path) {
		this.path = path;
	}

	public final int getDepth() {
		return depth;
	}

	public final void setDepth(int depth) {
		this.depth = depth;
	}
}
