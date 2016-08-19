package com.crazybird.scene;

import org.cocos2d.layers.CCScene;

import android.content.Context;

import com.crazybird.App;
import com.crazybird.layer.BackgroundLayer;
import com.crazybird.layer.MultiLayer;
import com.crazybird.layer.TransitionLayer;

public class MultiScene extends CCScene {

	public MultiScene(boolean isBird, Context context) {
		this.addChild(new BackgroundLayer());
		App.multi = new MultiLayer(isBird, context);
		this.addChild(App.multi);
		TransitionLayer transitionPlayer = new TransitionLayer();
		transitionPlayer.setTag((10100));
		this.addChild(transitionPlayer);
	}

	public void onReceive(String command) {
		if (!command.contains("game")) {
			return;
		}
		String[] str = command.split("\\|");
		if (str.length < 2) {
			return;
		}
		App.multi.onReceive(command.substring(str[0].length() + 1));
	}

}
