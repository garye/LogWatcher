package org.graysky.eclipse.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.graysky.eclipse.logwatcher.LogwatcherPlugin;

/**
 * Utilities for dealing with images, icons, etc.
 */
public class ImageUtils {

	/**
	 * Create an image descriptor for the given filename (relative to the plugin
	 * install directory)
	 */
	public static ImageDescriptor createImageDescriptor(String filename) {
		try {
			URL url = new URL(LogwatcherPlugin.getDefault().getBundle()
					.getEntry("/"), filename);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			return null;
		}
	}
}
