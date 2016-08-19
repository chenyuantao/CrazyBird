package com.crazybird.scene;

import org.cocos2d.layers.CCScene;

import com.crazybird.layer.BackgroundLayer;
import com.crazybird.layer.TransitionLayer;
import com.crazybird.layer.WhichLayer;

public class WhichScene extends CCScene {
	
	public WhichScene(){
		BackgroundLayer bg = new BackgroundLayer();
		bg.disapperLand();
		this.addChild(bg);
		this.addChild(new WhichLayer());
		TransitionLayer transitionWhich =new TransitionLayer();
		transitionWhich.setTag((10100));
		this.addChild(transitionWhich);	
	}

}
