package com.crazybird.layer;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGSize;

public class TransitionLayer extends CCLayer {

	private CGSize screenSize;
	private CCSprite black;
	private CCScene from;
	private CCScene to;
	
	public TransitionLayer(){
		black = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("black.png"));
		screenSize = CCDirector.sharedDirector().winSize();
		black.setScaleX(screenSize.width/32);
		black.setScaleY(screenSize.height/32);
		black.setOpacity(0);
		black.setPosition(screenSize.width/2,screenSize.height/2);
		this.addChild(black);
	}
	
	public void transition(CCScene from,CCScene to){
		this.from=from;
		this.to=to;
		black.runAction(CCSequence.actions(CCFadeIn.action(0.4f), CCCallFuncN.action(this,"onMoveFinish"),CCFadeOut.action(0.3f),CCCallFuncN.action(this, "onTransitionFinish")));
	}
	
	public void onMoveFinish (Object sender) {
		for(CCNode node: from.getChildren()){
			node.setVisible(false);
		}	
	}
	
	public void onTransitionFinish(Object sender){		
		CCDirector.sharedDirector().replaceScene(to);
		from.removeSelf();
	}
	
}
