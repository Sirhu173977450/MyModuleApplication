package com.example.nmproj221;

import java.io.File;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.VideoAttributes;
import it.sauronsoftware.jave.VideoSize;

public class FaveUtil {

	private static FaveUtil instance = new FaveUtil();

	private FaveUtil() {
	}

	public static FaveUtil getInstance() {
		return instance;
	}

	/**
	 * 压缩视频（固定压缩成mp4格式＿
	 *
	 * @param oldFilePath
	 *            for example d://zcx.mp4
	 * @param newFilePath
	 *            for example d://zcx2.mp4
	 * @return true=成功，false=失败
	 */
	public boolean compressVideo(String oldFilePath, String newFilePath) {
		File oldFile = new File(oldFilePath);
		File newFile = new File(newFilePath);
		long starttime = 0;
		long endtime = 0;
		boolean result = false;

		if (!oldFile.exists()) {
			System.out.println("----原文件不存在");
			return false;
		}

		String newFileType = newFilePath.substring(newFilePath.lastIndexOf("."));
		if (!newFileType.equalsIgnoreCase(".mp4")) {
			System.out.println("----新文件类型不寿");
			return false;
		}

		try {
			starttime = System.currentTimeMillis();
			System.out.println("----starttime=" + starttime);
			AudioAttributes audio = new AudioAttributes();
			audio.setCodec("libfaac");
			audio.setBitRate(14000);
			audio.setChannels(1);
			audio.setSamplingRate(11025);
			VideoAttributes video = new VideoAttributes();
			video.setCodec("mpeg4");
			video.setBitRate(120000);
			// video.setFrameRate(15);
			video.setSize(new VideoSize(400, 300));
			EncodingAttributes attrs = new EncodingAttributes();
			attrs.setFormat("mp4");
			attrs.setAudioAttributes(audio);
			attrs.setVideoAttributes(video);
			Encoder encoder = new Encoder();
			encoder.encode(oldFile, newFile, attrs);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			endtime = System.currentTimeMillis();
			System.out.println("----endtime=" + endtime);
			System.out.println("----usetime=" + (endtime - starttime));
			System.out.println("----result=" + result);
		}
		return result;
	}
}