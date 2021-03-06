package com.example.mobilemedia.lyric;

import android.os.Environment;

import com.example.mobilemedia.util.StringUtil;

import java.io.File;

/**
 * 模拟歌词加载模块
 * @author Administrator
 * 
 */
public class LyricLoader {
	private static String LYRIC_DIR = Environment.getExternalStorageDirectory()+"/test/audio";
	public static File loadLyricFileByName(String name){
		File file = new File(LYRIC_DIR,StringUtil.formatAudioName(name)+".lrc");
		if(!file.exists()){
			 file = new File(LYRIC_DIR, StringUtil.formatAudioName(name)+".txt");
		}
		return file;
	}
}
