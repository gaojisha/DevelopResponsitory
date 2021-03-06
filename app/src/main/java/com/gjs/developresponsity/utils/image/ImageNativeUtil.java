package com.gjs.developresponsity.utils.image;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;


public class ImageNativeUtil {
	private static int DEFAULT_QUALITY = 95;

	public static void compressBitmap(Bitmap bit, String fileName, boolean optimize, int level) {
		compressBitmap(bit, DEFAULT_QUALITY, fileName, optimize, level);

	}

	public static void compressBitmap(Bitmap bit, int quality, String fileName, boolean optimize, int level) {
		// Log.d("native", "compress of native");
		if (bit.getConfig() != Config.ARGB_8888) {
			Bitmap result = null;

			result = Bitmap.createBitmap(bit.getWidth() / level, bit.getHeight() / level, Config.ARGB_8888);
			Canvas canvas = new Canvas(result);
			Rect rect = new Rect(0, 0, bit.getWidth() / level, bit.getHeight() / level);
			canvas.drawBitmap(bit, null, rect, null);
			saveBitmap(result, quality, fileName, optimize);
			result.recycle();
		} else {
			saveBitmap(bit, quality, fileName, optimize);
		}

	}

	private static String getFileType(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		String type = options.outMimeType;
		if (TextUtils.isEmpty(type)) {
			type = "";
		} else {
			type = type.substring(6, type.length());
		}
		return type;
	}

	public static void compressBitmap(String input, String output, boolean optimize, ImageTools.Quality q) {
		String fileType = getFileType(input);
		int fileTypeTag = -1;
		switch (fileType) {
			case "jpg":
				fileTypeTag = 1;
				break;
			case "jpeg":
				fileTypeTag = 1;
				break;
			case "bmp":
				fileTypeTag = 1;
				break;
			case "png":
				fileTypeTag = 2;
				break;
			default:
				fileTypeTag = -1;
				break;
		}
		zoomcompress(input.getBytes(), output.getBytes(), optimize, q.getQuality(), fileTypeTag);
	}

	public static void compressBitmapCut(String input, String output, boolean optimize, ImageTools.Quality q, int s_x, int s_y, int e_x, int e_y) {
		zoomcompressCut(input.getBytes(), output.getBytes(), optimize, q.getQuality(), s_x, s_y, e_x, e_y);
	}

	private static void saveBitmap(Bitmap bit, int quality, String fileName, boolean optimize) {
		compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality, fileName.getBytes(), optimize);
	}

	private static native String compressBitmap(Bitmap bit, int w, int h, int quality, byte[] fileNameBytes,
												boolean optimize);

	private static native long zoomcompress(byte[] input, byte[] output, boolean optimize, int q, int fileTypeTag);


	private static native long zoomcompressCut(byte[] input, byte[] output, boolean optimize, int q, int s_x, int s_y, int e_x, int e_y);

	static {
		// System.loadLibrary("jpegcompress");
		System.loadLibrary("jpegcompressjni");
	}
}
