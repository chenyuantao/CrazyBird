package com.crazybird;

import java.util.HashMap;

import org.apache.http.Header;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.opengl.CCGLSurfaceView;
import com.crazybird.layer.BackgroundLayer;
import com.crazybird.layer.SingleLayer;
import com.crazybird.layer.TransitionLayer;
import com.crazybird.layer.WhichLayer;
import com.crazybird.scene.SingleScene;
import com.crazybird.scene.WhichScene;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.Window;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
	static {
		System.loadLibrary("gdx");
	}
	CCGLSurfaceView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new CCGLSurfaceView(this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		App.context = this;
		App.sharedPreferences = getSharedPreferences("score",
				Activity.MODE_PRIVATE);
		// 载入资源文件
		CCSpriteFrameCache.sharedSpriteFrameCache().addSpriteFrames(
				"resource.plist");
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		CCDirector director = CCDirector.sharedDirector();
		director.setDeviceOrientation(CCDirector.kCCDeviceOrientationPortrait);
		director.attachInView(view);
		director.setAnimationInterval(1 / 30.0); // 每帧所需要的时间，意思是30FPS
		setContentView(view);
		if (director.winSize().height > 1735) {
			director.setScreenSize(director.winSize().width, 1735);
			FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view
					.getLayoutParams();
			params.height = 1735;
			// params.gravity = Gravity.CENTER;
			params.gravity = Gravity.TOP;
			view.setLayoutParams(params);
		}
		// 设置选择场景
		App.whichScene = new WhichScene();
		director.runWithScene(App.whichScene);
		App.soundPoolMap.put(App.RAW_FLY,
				App.soundPool.load(this, R.raw.fly, 1));
		App.soundPoolMap.put(App.RAW_SCORE,
				App.soundPool.load(this, R.raw.score, 1));
		App.soundPoolMap.put(App.RAW_DEAD,
				App.soundPool.load(this, R.raw.dead, 1));
		App.soundPoolMap.put(App.RAW_SHOW,
				App.soundPool.load(this, R.raw.show, 1));
		App.soundPoolMap.put(App.RAW_WIN,
				App.soundPool.load(this, R.raw.win, 1));
		App.soundPoolMap.put(App.RAW_LOSE,
				App.soundPool.load(this, R.raw.lose, 1));
	}
}
