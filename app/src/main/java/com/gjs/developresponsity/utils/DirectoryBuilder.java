package com.gjs.developresponsity.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.text.TextUtils;

import com.gjs.developresponsity.utils.base.FileUtils;
import com.gjs.developresponsity.utils.base.SDCardUtils;

public class DirectoryBuilder {

	public static String ROOT_NAME = null;

	public static final String SDCARD_ROOT = SDCardUtils.getSDCardPathWithFileSeparators();

	public static final String SYS_DATA_DIR_ROOT = "";

	public static final String AVATAR = "avatar";
	public static final String SOCIAL = "social";

	public static final String THUMB = "thumb";

	public static final String TEMP = "temp";

	public static final String HD = "hd";

	public static final String AVATAR_THUMB = AVATAR + File.separator + THUMB;

	public static final String AVATAR_HD = AVATAR + File.separator + HD;

	// public static final String SYS_DATA_DIR_ROOT = CoreContext.getInstance().getContext().getFilesDir().getPath() + File.separator;

	public static String RCS_ROOT = null;

	public static final String RCS_DATA_DIR_ROOT = SYS_DATA_DIR_ROOT + ROOT_NAME + File.separator;

	public static String DIR_IMAGE = null;

	public static String DIR_EMOTICON = null;

	public static String DIR_EMOTICON_SINGLE = null;

	public static String DIR_EMOTICON_THUMB = null;

	public static String DIR_EMOTICON_DETAIL = null;

	public static String DIR_VOICE = null;

	public static String DIR_FILE = null;

	public static String DIR_THEME = null;

	public static String DATA_DIR_EMOTICON = null;

	public static String DATA_DIR_EMOTICON_GIF = null;

	public static String DIR_AVATAR = null;

	public static String DIR_AVATAR_HD = null;

	public static String DIR_SOCIAL = null;

	public static String DIR_SOCIAL_COVER = null;
	
	public static String DIR_SOCIAL_IMG = null;

	public static String DIR_AVATAR_THUMB = null;

	public static String DIR_AVATAR_TEMP = null;

	public static String DIR_MAP_SNAPSHOT = null;

	// 以下2值用于趣拍视频文件存放路径
	public static String FUNNY_VIDEO_DIR = null;
	public static final String VIDEO_TMPDIR = "video_tmpdir";

	public static final String AVATAR_FILE_FORMAT = ".png";
	
	public static void updateRootName (String name) {		
//		Trace.T(name);
		ROOT_NAME = name;
		RCS_ROOT = SDCARD_ROOT + ROOT_NAME + File.separator;
		DIR_IMAGE = RCS_ROOT + "image" + File.separator;
		DIR_EMOTICON = RCS_ROOT + "emoticon" + File.separator;
		DIR_EMOTICON_SINGLE = DIR_EMOTICON + "single" + File.separator;
		DIR_EMOTICON_THUMB = DIR_EMOTICON + THUMB + File.separator;
		DIR_EMOTICON_DETAIL = DIR_EMOTICON + "detail" + File.separator;
		DIR_VOICE = RCS_ROOT + "voice" + File.separator;
		DIR_FILE = RCS_ROOT + "file" + File.separator;
		DIR_THEME = RCS_ROOT + "theme" + File.separator;
		DATA_DIR_EMOTICON = SYS_DATA_DIR_ROOT + "emotcion" + File.separator;
		DATA_DIR_EMOTICON_GIF = DATA_DIR_EMOTICON + "gif" + File.separator;
		DIR_AVATAR = RCS_ROOT + AVATAR + File.separator;
		DIR_AVATAR_HD = DIR_AVATAR + HD + File.separator;
		DIR_SOCIAL = RCS_ROOT + SOCIAL + File.separator;
		DIR_SOCIAL_COVER = DIR_SOCIAL + "cover" + File.separator;		
		DIR_SOCIAL_IMG = DIR_SOCIAL + "image" + File.separator;
		DIR_AVATAR_THUMB = DIR_AVATAR + THUMB + File.separator;
		DIR_AVATAR_TEMP = DIR_AVATAR + TEMP + File.separator;
		DIR_MAP_SNAPSHOT = RCS_ROOT + "map" + File.separator;

		FUNNY_VIDEO_DIR = RCS_ROOT + "video" + File.separator;
	}

	/**
	 * get the absolute full name with extention of the portrait image file
	 * 
	 * @param userId
	 *            the userId of the contact
	 * @param key
	 *            the portraitId of the contact`s portrait
	 * @param isthumb
	 *            thumb file or origin file
	 * @return
	 */
	public static String getAvatarFullFileName(long userId, String portraitId, boolean isthumb) {
		String dir = (isthumb) ? DIR_AVATAR_THUMB : DIR_AVATAR_HD;
		return dir + getAvatarFileNameWithExtention(userId, portraitId);
	}

	/**
	 * get the file name with file extention of contact portrait
	 * 
	 * @param userId
	 *            the userId of the contact
	 * @param key
	 *            the portraitId of the contact`s portrait
	 * @return
	 */
	public static String getAvatarFileNameWithExtention(long userId, String portraitId) {
		return getAvatarFileName(userId, portraitId) + AVATAR_FILE_FORMAT;
	}

	/**
	 * get the file name without file extention of contact portrait
	 * 
	 * @param userId
	 *            the userId of the contact
	 * @param key
	 *            the portraitId of the contact`s portrait
	 * @return
	 */
	public static String getAvatarFileName(long userId, String portraitId) {
		if (TextUtils.isEmpty(portraitId)) {
			return String.valueOf(userId);
		}
		return String.valueOf(userId) + "_" + portraitId;
	}

	public static void createDir() {
		FileUtils.prepareDir(DIR_IMAGE);
		FileUtils.prepareDir(DIR_VOICE);
		FileUtils.prepareDir(DIR_THEME);
		FileUtils.prepareDir(DIR_FILE);

		FileUtils.prepareDir(DIR_AVATAR);
		FileUtils.prepareDir(DIR_AVATAR_HD);
		FileUtils.prepareDir(DIR_AVATAR_THUMB);
		FileUtils.prepareDir(DIR_AVATAR_TEMP);

		FileUtils.prepareDir(DIR_EMOTICON);
		FileUtils.prepareDir(DIR_EMOTICON_DETAIL);
		FileUtils.prepareDir(DIR_EMOTICON_SINGLE);
		FileUtils.prepareDir(DIR_EMOTICON_THUMB);
		FileUtils.prepareDir(FUNNY_VIDEO_DIR);
		FileUtils.prepareDir(DIR_MAP_SNAPSHOT);
		
		FileUtils.prepareDir(DIR_SOCIAL_IMG);
		FileUtils.prepareDir(DIR_SOCIAL_COVER);
		createNomedia();
	}

	private static void createNomedia() {
		File file = new File(RCS_ROOT + "/.nomedia");
		saveByteToFile(file, new byte[] {});
	}

	public static String getSystemDataDir(Context context) {
		return context.getFilesDir().getPath();
	}

	private static void saveByteToFile(File f, byte[] buff) {
		FileOutputStream fOut = null;
		try {
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			fOut = new FileOutputStream(f);
			fOut.write(buff);
			fOut.flush();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (fOut != null) {
					fOut.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}