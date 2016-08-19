package com.crazybird;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.opengl.CCGLSurfaceView;

import com.crazybird.bluetooth.Bluetooth;
import com.crazybird.bluetooth.Bluetooth.CommandListener;
import com.crazybird.scene.CharacterScene;
import com.crazybird.scene.WhichScene;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MultiplayActivity extends Activity {

	CCGLSurfaceView view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new CCGLSurfaceView(this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// 载入资源文件
		CCSpriteFrameCache.sharedSpriteFrameCache().addSpriteFrames(
				"resource2.plist");
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
			params.gravity = Gravity.TOP;
			view.setLayoutParams(params);
		}
		App.characterScene = new CharacterScene(Bluetooth.isClient, this);
		App.playRaw(App.RAW_SCORE);
		director.runWithScene(App.characterScene);
		Bluetooth.setCommandListener(new CommandListener() {

			@Override
			public void onShutDown() {
				App.playRaw(App.RAW_SHOW);
				Toast.makeText(MultiplayActivity.this, "对方不想和你玩游戏并且断开了连接",
						Toast.LENGTH_SHORT).show();
				MultiplayActivity.this.finish();
			}

			@Override
			public void onReceiveCommand(String command) {
				if (command.contains("game")) {
					if (App.multiScene != null) {
						App.multiScene.onReceive(command);
					}
				} else {
					if (App.characterScene != null) {
						App.characterScene.drawSelect(command);
					}
				}

			}
		});

	}

	@Override
	protected void onDestroy() {
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
		if (Bluetooth.isClient) {
			Bluetooth.stopClient();
		} else {
			Bluetooth.stopServer();
		}
		super.onDestroy();
	}

}
