package com.crazybird.scene;

import org.cocos2d.layers.CCScene;

import android.content.Context;

import com.crazybird.layer.CharacterLayer;
import com.crazybird.layer.TransitionLayer;

public class CharacterScene extends CCScene {
	
	private CharacterLayer mCharacterLayer;
	
	public CharacterScene(boolean isClient,Context context){
		mCharacterLayer = new CharacterLayer(isClient,context);
		this.addChild(mCharacterLayer);
		TransitionLayer transitionPlayer =new TransitionLayer();
		transitionPlayer.setTag((10100));
		this.addChild(transitionPlayer);
	}
	
	public void drawSelect(String command){
		mCharacterLayer.drawSelect(command);
	}

}
