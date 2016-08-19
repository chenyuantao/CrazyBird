package com.crazybird.layer;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCPlace;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import com.crazybird.App;

public class BackgroundLayer extends CCLayer {

	private CGSize screenSize;
	private CCSprite background; // 宽288 高512
	private CCSprite land1; // 宽288 高112
	private CCSprite land2;
	private CCSprite lland; // 宽432 高112

	public BackgroundLayer() {
		screenSize = CCDirector.sharedDirector().winSize();

		background = CCSprite.sprite(CCSpriteFrameCache
				.sharedSpriteFrameCache().getSpriteFrame("background.png"));
		background.setScaleX(screenSize.getWidth() / 288);
		background.setScaleY(screenSize.getHeight() / 512);
		App.scaleX = screenSize.getWidth() / 288;
		App.scaleY = screenSize.getHeight() / 512;

		background.setPosition(screenSize.getWidth() * 0.5f,
				screenSize.getHeight() * 0.5f);
		this.addChild(background);

		lland = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("lland.png"));
		lland.setScaleX(App.scaleX);
		lland.setScaleY(App.scaleY);
		lland.setAnchorPoint(0f,0f);
		lland.setPosition(0, 0);
		lland.runAction(CCRepeatForever.action(CCSequence.actions(CCMoveTo.action(1.35f, CGPoint.ccp(-144*App.scaleX, 0f)), CCPlace.action(CGPoint.ccp(0, 0)))));
		this.addChild(lland);

		// land1 = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
		// .getSpriteFrame("land.png"));
		// land1.setScaleX(App.scaleX);
		// land1.setScaleY(App.scaleX);
		// land1.setPosition(screenSize.getWidth()*0.5f, App.scaleX*112*0.5f);
		//
		// land2 = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
		// .getSpriteFrame("land.png"));
		// land2.setScaleX(App.scaleX);
		// land2.setScaleY(App.scaleX);
		// land2.setPosition(screenSize.getWidth()*1.5f, App.scaleX*112*0.5f);
		//
		// CCMoveTo moveTo1 = CCMoveTo.action(3f,
		// CGPoint.ccp(-screenSize.getWidth()*0.5f, App.scaleX*112*0.5f));
		// CCPlace place1 =
		// CCPlace.action(CGPoint.ccp(screenSize.getWidth()*0.5f,
		// App.scaleX*112*0.5f));
		// CCSequence sequence1 = CCSequence.actions(moveTo1, place1);
		// CCRepeatForever repeat1 = CCRepeatForever.action(sequence1);
		//
		//
		// CCMoveTo moveTo2 = CCMoveTo.action(3f,
		// CGPoint.ccp(screenSize.getWidth()*0.5f, App.scaleX*112*0.5f));
		// CCPlace place2 =
		// CCPlace.action(CGPoint.ccp(screenSize.getWidth()*1.5f,
		// App.scaleX*112*0.5f));
		// CCSequence sequence2 = CCSequence.actions(moveTo2, place2);
		// CCRepeatForever repeat2 = CCRepeatForever.action(sequence2);
		//
		// land1.runAction(repeat1);
		// land2.runAction(repeat2);
		//
		// this.addChild(land1);
		// this.addChild(land2);
	}

	public void showLand() {
//		land1.setVisible(true);
//		land2.setVisible(true);
		lland.setVisible(true);
	}

	public void disapperLand() {
//		land1.setVisible(false);
//		land2.setVisible(false);
		lland.setVisible(false);
	}

}
