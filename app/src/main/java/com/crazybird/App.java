package com.crazybird;

import java.util.HashMap;
import java.util.Random;

import org.cocos2d.layers.CCScene;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.SoundPool;

import com.crazybird.layer.MultiLayer;
import com.crazybird.layer.SingleLayer;
import com.crazybird.scene.CharacterScene;
import com.crazybird.scene.MultiScene;

public class App extends Application {
	public static final int POKEBALL = 0; // 精灵球
	public static final int BANANA = 1; // 香蕉
	public static final int YELLOW = 2; // 小黄鸟
	public static final int RAW_FLY = 0; // 翅膀声音
	public static final int RAW_SCORE = 1; // 得分声音
	public static final int RAW_DEAD = 2; // 得分声音
	public static final int RAW_SHOW = 3; // 出场声音
	public static final int RAW_WIN =4; // 胜利声音
	public static final int RAW_LOSE = 5; // 失败声音
	public static int which = POKEBALL; // 确定当前运行的是哪个游戏
	public static float scaleX; // 精灵x拉伸系数
	public static float scaleY; // 精力y拉伸系数
	public static SingleLayer single; // 单机游戏层
	public static MultiLayer multi; // 多人游戏层
	public static CCScene whichScene; // 选择场景
	public static CCScene singleScene; // 游戏场景
	public static CharacterScene characterScene; // 角色场景
	public static MultiScene multiScene; // 多人游戏场景
	public static int best = 0; // 个人最好成绩
	public static int global = -1; // 世界成绩
	public static SharedPreferences sharedPreferences;
	public static Context context;
	public static SoundPool soundPool;
	public static HashMap<Integer, Integer> soundPoolMap;

	public App() {
		App.soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 100);
		App.soundPoolMap = new HashMap<Integer, Integer>();
	}
	
	public static void playRaw(int raw){
		App.soundPool.play(App.soundPoolMap.get(raw), 1, 1, 0, 0, 1);
	}
	
	public static void saveBest(int score) {
		Editor editor = sharedPreferences.edit();
		editor.putInt("best" + which, score);
		editor.commit();
	}

	public static int getBest() {
		return sharedPreferences.getInt("best" + which, 0);
	}

	public static float getRandom(float min, float max) {
		Random r = new Random();
		r.setSeed(System.currentTimeMillis());
		float f = r.nextFloat() * (max - min) + min;
		return f;
	}

	public static int getRandomInt(int max) {
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		int i = rand.nextInt(max); // 生成0-100以内的随机数
		return i;
	}
}
