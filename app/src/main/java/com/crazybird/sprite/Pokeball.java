package com.crazybird.sprite;

import java.util.ArrayList;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCJumpBy;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;

import com.crazybird.App;
import com.crazybird.bluetooth.Bluetooth;

public class Pokeball extends Monster {
	private ArrayList<CCSprite> pokeballs;
	private ArrayList<Float> heights;
	private ArrayList<Float> distances;

	public Pokeball() {
		if (App.single != null) {
			App.single.addListener(listener);
		}
		init();
	}

	public Pokeball(float height) {
		if (App.multi != null) {
			App.multi.addListener(listener);
		}
		this.height = height;
		init();
		shootPokeball();
	}

	private void init() {
		pokeballs = new ArrayList<CCSprite>();
		heights = new ArrayList<Float>();
		distances = new ArrayList<Float>();
	}

	@Override
	protected void onDestory() {
	}

	@Override
	protected void onCollision(CCSprite bird) {
		for (CCSprite pokeball : pokeballs) {
			if (CGRect.intersects(smallBall(pokeball), smallRect(bird))) {
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
		int k = (int) (time * 10); // 0.4秒开始 ，每0.6秒弹出一个球
		int yushu = (k - 4) % 6;
		int shang = (k - 4) / 6;
		if (yushu == 0 && shang == pokeballs.size()) {
			shootPokeball();
		}
	}

	private void shootPokeball() {
		float height = 0f;
		if (this.height == 0) {
			height = App.getRandom(150, 550) * App.scaleY;
		} else {
			height = this.height - 145 * App.scaleY;
		}
		float distance = ((height / App.scaleY - 150) / 400 * 130 + 120)
				* App.scaleX;
		CCSprite pokeball = CCSprite.sprite(CCSpriteFrameCache
				.sharedSpriteFrameCache().getSpriteFrame("pokeball.png")); // 80*80
		pokeball.setScale(App.scaleX);
		pokeball.setPosition(CGPoint.ccp(screenSize.width + distance / 2,
				145 * App.scaleY));
		pokeball.runAction(CCRepeatForever.action(CCRotateBy.action(0.2f, -90f)));
		pokeball.runAction(CCSequence.actions(CCJumpBy.action(0.5f + height
				/ screenSize.height * 1.5f, CGPoint.ccp(-distance, 0), height,
				1), CCCallFuncN.action(this, "onJumpFinish")));
		if (this.height == 0f) {
			App.single.addChild(pokeball);
		} else {
			App.multi.addChild(pokeball);
		}
		pokeballs.add(pokeball);
		heights.add(height);
		distances.add(distance);
	}

	public void onJumpFinish(Object sender) {
		CCSprite pokeball = (CCSprite) sender;
		for (int i = 0, len = pokeballs.size(); i < len; i++) {
			if (pokeballs.get(i).equals(pokeball)) {
				float height = heights.get(i);
				float distance = distances.get(i);
				height = height / 4 * 3;
				distance = distance / 100 * 95;
				if (pokeball.getPosition().x < -20 * App.scaleX) {
					pokeball.removeSelf();
				} else {
					pokeball.runAction(CCSequence.actions(CCJumpBy.action(0.5f
							+ height / screenSize.height * 1.5f,
							CGPoint.ccp(-distance, 0), height, 1), CCCallFuncN
							.action(this, "onJumpFinish")));
					heights.set(i, height);
					distances.set(i, distance);
				}

			}
		}
	}

	private CGRect smallBall(CCNode sprite) {
		CGRect bigR = sprite.getBoundingBox();
		CGRect smallR = CGRect.make(
				bigR.origin.x + (float) ((40 - 20 * Math.sqrt(Math.PI)) / 2)
						* App.scaleX,
				bigR.origin.y - (float) ((40 - 20 * Math.sqrt(Math.PI)) / 2)
						* App.scaleY, (float) (20 * Math.sqrt(Math.PI))
						* App.scaleX, (float) (20 * Math.sqrt(Math.PI))
						* App.scaleY);
		return smallR;
	}

}
