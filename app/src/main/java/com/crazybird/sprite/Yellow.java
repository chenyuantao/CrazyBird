package com.crazybird.sprite;

import java.util.ArrayList;

import org.cocos2d.actions.ease.CCEaseIn;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import com.crazybird.App;
import com.crazybird.bluetooth.Bluetooth;

public class Yellow extends Monster {
	private ArrayList<CCSprite> yellows;
	private ArrayList<CCSpriteFrame> framesYellow;

	public Yellow() {
		if (App.single != null) {
			App.single.addListener(listener);
		}
		init();		
	}

	public Yellow(float height,float birdX,float birdY) {
		if (App.multi != null) {
			App.multi.addListener(listener);
		}
		this.birdX=birdX;
		this.birdY=birdY;
		this.height = height;
		init();		
		shootYellow();
	}

	private void init() {
		yellows = new ArrayList<CCSprite>();
		framesYellow = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 4; i++) {
			framesYellow.add(CCSpriteFrameCache.sharedSpriteFrameCache()
					.getSpriteFrame("yellow" + i + ".png"));
		}
	}

	@Override
	protected void onDestory() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onCollision(CCSprite bird) {
		for (CCSprite yellow : yellows) {
			if (CGRect.intersects(yellow.getBoundingBox(), smallRect(bird))) {
				if (height == 0f) {
					App.single.gameover();
				} else {
					if (App.multi.isBird) {
						Bluetooth
								.sendMessageHandle("<command,game|over|true>,");
						App.multi.gameover(false);
					}
				}
			}
		}
	}

	@Override
	protected void onUpdate(CCSprite bird) {
		// 从0.3秒开始算起，每0.4秒发射一次小黄
		int k = (int) (time * 10);
		int shang = (k - 3) / 4;
		int yushu = (k - 3) % 4;
		if (yushu == 0 && shang == yellows.size()) {
			shootYellow();
		}
	}

	private void shootYellow() { // 44*33
		CCSprite yellow = CCSprite.sprite(CCSpriteFrameCache
				.sharedSpriteFrameCache().getSpriteFrame("yellow0.png"));
		yellow.setScale(App.scaleX);
		if (height == 0f) {
			yellow.setPosition(screenSize.width + 22 * App.scaleX,
					App.getRandom((112f + 18f), (512f - 18f)) * App.scaleY);
		} else {
			yellow.setPosition(screenSize.width + 22 * App.scaleX, height);
		}
		CCSpawn spawn_show = CCSpawn.actions(
				CCMoveBy.action(0.3f, CGPoint.ccp(-88 * App.scaleX, 0)),
				CCRotateBy.action(0.3f, 360f));
		CCSpawn spawn_speedUp = CCSpawn.actions(CCAnimate.action(CCAnimation
				.animation("yellow", 0.2f, framesYellow)), CCEaseIn.action(
				CCMoveTo.action(0.5f, CGPoint.ccp(-22 * App.scaleX, birdY)),
				2.0f));
		spawn_show.setDuration(0.5f);
		CCSequence seq = CCSequence.actions(
				spawn_show,
				CCRotateBy.action(
						0.01f,
						(float) Math.atan((birdY - yellow.getPosition().y)
								/ (yellow.getPosition().x - birdX))),
				spawn_speedUp, CCCallFuncN.action(this, "onBirdsFinish"));
		yellow.runAction(seq);
		if (height == 0f) {
			App.single.addChild(yellow);
		} else {
			App.multi.addChild(yellow);
		}
		yellows.add(yellow);
	}

}
