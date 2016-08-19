package com.crazybird.scene;

import org.cocos2d.layers.CCScene;

import com.crazybird.App;
import com.crazybird.layer.BackgroundLayer;
import com.crazybird.layer.SingleLayer;
import com.crazybird.layer.TransitionLayer;

public class SingleScene extends CCScene {
	
	public SingleScene(){		
		App.single = new SingleLayer();
		this.addChild( new BackgroundLayer());
		this.addChild(App.single);
		TransitionLayer transitionPlayer =new TransitionLayer();
		transitionPlayer.setTag((10100));
		this.addChild(transitionPlayer);
	}
	

}
