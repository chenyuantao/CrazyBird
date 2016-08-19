package com.crazybird.sprite;

import java.util.ArrayList;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.ease.CCEaseIn;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCBezierTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CCBezierConfig;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import com.crazybird.App;
import com.crazybird.bluetooth.Bluetooth;

public class Banana extends Monster {
	private ArrayList<CCSprite> bananas;

	public Banana() {
		if (App.single != null) {
			App.single.addListener(listener);
		}
		init();
	}

	public Banana(float height) {
		if (App.multi != null) {
			App.multi.addListener(listener);
		}
		this.height = height;
		init();
		shootBanana();
	}

	private void init() {
		bananas = new ArrayList<CCSprite>();
	}

	@Override
	protected void onDestory() {
	}

	@Override
	protected void onCollision(CCSprite bird) {
		for (CCSprite banana : bananas) {
			if (CGRect.intersects(banana.getBoundingBox(), smallRect(bird))) {
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
		int k = (int) (time * 10); // 0.3秒开 ，每0.6秒发射一次
		int yushu = (k - 3) % 6;
		int shang = (k - 3) / 6;
		if (yushu == 0 && shang == bananas.size()) {
			shootBanana();
		}
	}

	public void onFinish(Object sender) {
		CCSprite s = (CCSprite) sender;
		s.removeSelf();
	}

	private void shootBanana() {
		// 回旋镖
		CCSprite banana = CCSprite.sprite(CCSpriteFrameCache
				.sharedSpriteFrameCache().getSpriteFrame("banana.png"));
		if (height == 0f) {
			banana.setPosition(screenSize.width + 20 * App.scaleX,
					App.getRandom(132, 452) * App.scaleY);
		} else {
			banana.setPosition(screenSize.width + 20 * App.scaleX, height);
		}
		banana.setScale(App.scaleX);
		// 回旋动作
		CCRepeatForever repeatRotate = CCRepeatForever.action(CCRotateBy
				.action(0.5f, -360f));
		// 贝斯曲线抛出
		CCBezierConfig configFlyingTo = new CCBezierConfig();
		configFlyingTo.endPosition = CGPoint.ccp(20 * App.scaleX,
				banana.getPosition().y);
		configFlyingTo.controlPoint_2 = CGPoint.ccp(
				configFlyingTo.endPosition.x, configFlyingTo.endPosition.y
						+ 100 * App.scaleY);
		configFlyingTo.controlPoint_1 = CGPoint.ccp(screenSize.width + 20
				* App.scaleX, banana.getPosition().y + 100 * App.scaleY);
		CCEaseIn easeFlyingTo = CCEaseIn.action(
				CCBezierTo.action(1.35f, configFlyingTo), 0.9f);
		// 贝斯曲线抛回
		CCBezierConfig configFlyingBack = new CCBezierConfig();
		configFlyingBack.endPosition = CGPoint.ccp(screenSize.width + 20
				* App.scaleX, banana.getPosition().y);
		configFlyingBack.controlPoint_2 = CGPoint.ccp(
				configFlyingBack.endPosition.x, configFlyingBack.endPosition.y
						- 100 * App.scaleY);
		configFlyingBack.controlPoint_1 = CGPoint.ccp(20 * App.scaleX,
				banana.getPosition().y - 100 * App.scaleY);
		CCEaseIn easeFlyingBack = CCEaseIn.action(
				CCBezierTo.action(0.85f, configFlyingBack), 2f);
		banana.runAction(repeatRotate);
		banana.runAction(CCSequence.actions(easeFlyingTo, easeFlyingBack,
				CCCallFuncN.action(this, "onFinish")));
		if (height == 0f) {
			App.single.addChild(banana);
		} else {
			App.multi.addChild(banana);
		}
		bananas.add(banana);
	}

}
