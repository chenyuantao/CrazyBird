package com.crazybird.layer;

import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;

import com.crazybird.App;
import com.crazybird.bluetooth.Bluetooth;
import com.crazybird.scene.MultiScene;

public class CharacterLayer extends CCLayer {
	private static final int NONE = 0;
	private static final int BIRD = 1;
	private static final int KILLER = 2;
	private Context mContext;
	private CGSize screenSize;
	private int select1p;
	private int select2p;
	private boolean isReady;
	CCSprite bg;
	CCSprite who;
	CCSprite btn_ok;
	CCSprite btn_exit;
	CCSprite p1;
	CCSprite p2;

	public CharacterLayer(boolean isClient, Context context) {		
		mContext = context;
		select1p = NONE;
		select2p = NONE;
		screenSize = CCDirector.sharedDirector().winSize();
		bg = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("vs.png"));
		bg.setScaleX(App.scaleX);
		bg.setScaleY(App.scaleY);
		bg.setPosition(CGPoint.ccp(screenSize.width / 2, screenSize.height / 2));
		this.addChild(bg);

		if (isClient) {
			who = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
					.getSpriteFrame("ur2p.png"));
		} else {
			who = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
					.getSpriteFrame("ur1p.png"));
		}
		who.setScaleX(App.scaleX);
		who.setScaleY(App.scaleY);
		who.setPosition(screenSize.width / 2, 170 * App.scaleY);
		this.addChild(who);
		if (isClient) {
			btn_ok = CCSprite.sprite(CCSpriteFrameCache
					.sharedSpriteFrameCache().getSpriteFrame("btn_ready.png"));
		} else {
			btn_ok = CCSprite.sprite(CCSpriteFrameCache
					.sharedSpriteFrameCache().getSpriteFrame(
							"btn_start_disable.png"));
		}
		btn_ok.setScale(App.scaleX);
		btn_ok.setPosition(86 * App.scaleX, 125 * App.scaleY);
		this.addChild(btn_ok);
		btn_exit = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("btn_2.png"));
		btn_exit.setScale(App.scaleX);
		btn_exit.setPosition(screenSize.width - 86 * App.scaleX,
				125 * App.scaleY);
		this.addChild(btn_exit);

		p1 = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("1p.png"));
		p1.setScaleX(App.scaleX);
		p1.setScaleY(App.scaleY);
		p1.setOpacity(0);
		this.addChild(p1);

		p2 = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("2p.png"));
		p2.setScaleX(App.scaleX);
		p2.setScaleY(App.scaleY);
		p2.setOpacity(0);
		this.addChild(p2);
		this.setIsTouchEnabled(true);
		isReady = false;
	}

	private void onOkClick() {
		if (isReady) {
			if (Bluetooth.isClient) {
				// 2P点击了取消，将图片替换为ready,并且发送取消信号
				btn_ok.setDisplayFrame(CCSpriteFrameCache
						.sharedSpriteFrameCache().getSpriteFrame(
								"btn_ready.png"));
				Bluetooth.sendMessageHandle("command,ready|0");
				isReady = false;
			} else {
				// 由于2P已经准备，进行双方的选人判断
				if (select1p == NONE) {
					showToast("1P尚未选择角色");
					return;
				}
				if (select2p == NONE) {
					showToast("2P尚未选择角色");
					return;
				}
				if (select1p == select2p) {
					showToast("双方不能选择相同的角色");
					return;
				}
				Bluetooth.sendMessageHandle("command,start|" + select2p);
				// 游戏开始
				App.playRaw(App.RAW_SHOW);
				TransitionLayer t = (TransitionLayer) App.characterScene
						.getChildByTag(10100);
				App.multiScene = new MultiScene(select1p == BIRD, mContext);
				t.transition(App.characterScene, App.multiScene);
			}
		} else {
			if (Bluetooth.isClient) {
				// 2P点击了准备,若已经选择了角色则将图片替换为cancel，并且发送准备信号
				if (select2p == NONE) {
					showToast("请先选择角色");
				} else {
					App.playRaw(App.RAW_SCORE);
					btn_ok.setDisplayFrame(CCSpriteFrameCache
							.sharedSpriteFrameCache().getSpriteFrame(
									"btn_cancel.png"));
					Bluetooth.sendMessageHandle("command,ready|1");
					isReady = true;
				}
			} else {
				// 1P点击了开始，但由于2P尚未准备，所以Toast提示
				showToast("对方尚未准备");
			}
		}
	}

	private void onExitClick() {
		Activity a = (Activity) mContext;
		a.finish();
	}

	public void drawSelect(String command) {
		if (!command.contains("|")) {
			return;
		}
		String[] str = command.split("\\|");
		if (str.length != 2) {
			return;
		}
		if (str[0].equals("start")) {
			int type = Integer.valueOf(str[1]);
			// 游戏开始
			App.playRaw(App.RAW_SHOW);
			TransitionLayer t = (TransitionLayer) App.characterScene
					.getChildByTag(10100);
			App.multiScene = new MultiScene(type == BIRD, mContext);
			t.transition(App.characterScene, App.multiScene);
		}
		if (str[0].equals("ready")) {
			int type = Integer.valueOf(str[1]);
			if (type == 1) {
				isReady = true;
				App.playRaw(App.RAW_SCORE);
				btn_ok.setDisplayFrame(CCSpriteFrameCache
						.sharedSpriteFrameCache().getSpriteFrame(
								"btn_start.png"));
			} else {
				isReady = false;
				btn_ok.setDisplayFrame(CCSpriteFrameCache
						.sharedSpriteFrameCache().getSpriteFrame(
								"btn_start_disable.png"));
			}
		}
		if (str[0].equals("c")) {
			int type = Integer.valueOf(str[1]);
			switch (type) {
			case BIRD:
				if (!Bluetooth.isClient) {
					// 2p
					p2.setOpacity(0);
					p2.setPosition(122 * App.scaleX, 308 * App.scaleY);
					p2.runAction(CCFadeIn.action(0.5f));
					select2p = BIRD;
				} else {
					// 1p
					p1.setOpacity(0);
					p1.setPosition(52 * App.scaleX, 308 * App.scaleY);
					p1.runAction(CCFadeIn.action(0.5f));
					select1p = BIRD;
				}
				break;
			case KILLER:
				if (!Bluetooth.isClient) {
					// 2p
					p2.setOpacity(0);
					p2.setPosition(235 * App.scaleX, 308 * App.scaleY);
					p2.runAction(CCFadeIn.action(0.5f));
					select2p = KILLER;
				} else {
					// 1p
					p1.setOpacity(0);
					p1.setPosition(163 * App.scaleX, 308 * App.scaleY);
					p1.runAction(CCFadeIn.action(0.5f));
					select1p = KILLER;
				}
				break;
			default:
				if (!Bluetooth.isClient) {
					// 2p
					p2.setOpacity(0);
					select2p = NONE;
				} else {
					// 1p
					p1.setOpacity(0);
					select1p = NONE;
				}
				break;
			}
			return;
		}

	}

	private void onSelect(int type) { // 55 303 左左 122左右
		switch (type) {
		case BIRD:
			if (Bluetooth.isClient) {
				// 2p
				p2.setOpacity(0);
				p2.setPosition(122 * App.scaleX, 308 * App.scaleY);
				p2.runAction(CCFadeIn.action(0.5f));
				select2p = BIRD;
			} else {
				// 1p
				p1.setOpacity(0);
				p1.setPosition(52 * App.scaleX, 308 * App.scaleY);
				p1.runAction(CCFadeIn.action(0.5f));
				select1p = BIRD;
			}
			Bluetooth.sendMessageHandle("command,c|" + BIRD);
			break;
		case KILLER:
			if (Bluetooth.isClient) {
				// 2p
				p2.setOpacity(0);
				p2.setPosition(235 * App.scaleX, 308 * App.scaleY);
				p2.runAction(CCFadeIn.action(0.5f));
				select2p = KILLER;
			} else {
				// 1p
				p1.setOpacity(0);
				p1.setPosition(163 * App.scaleX, 308 * App.scaleY);
				p1.runAction(CCFadeIn.action(0.5f));
				select1p = KILLER;
			}
			Bluetooth.sendMessageHandle("command,c|" + KILLER);
			break;
		default:
			if (Bluetooth.isClient) {
				// 2p
				p2.setOpacity(0);
				select2p = NONE;
			} else {
				// 1p
				p1.setOpacity(0);
				select1p = NONE;
			}
			Bluetooth.sendMessageHandle("command,c|" + NONE);
			break;
		}
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint point = CCDirector.sharedDirector().convertToGL(
				CGPoint.ccp(event.getX(), event.getY()));
		if (CGRect.containsPoint(btn_ok.getBoundingBox(), point)) {
			btn_ok.runAction(CCScaleTo.action(0.08f, 0.95f * App.scaleX));
		} else if (CGRect.containsPoint(btn_exit.getBoundingBox(), point)) {
			btn_exit.runAction(CCScaleTo.action(0.08f, 0.95f * App.scaleX));
		} else {
			CGRect left = CGRect.make(35 * App.scaleX, 188 * App.scaleY,
					110 * App.scaleX, 128 * App.scaleY);
			CGRect right = CGRect.make(148 * App.scaleX, 188 * App.scaleY,
					110 * App.scaleX, 128 * App.scaleY);
			if (CGRect.containsPoint(left, point)) {
				onSelect(BIRD);
			} else if (CGRect.containsPoint(right, point)) {
				onSelect(KILLER);
			} else {
				onSelect(NONE);
			}
		}
		return super.ccTouchesBegan(event);
	}

	@Override
	public boolean ccTouchesMoved(MotionEvent event) {
		CGPoint point = CCDirector.sharedDirector().convertToGL(
				CGPoint.ccp(event.getX(), event.getY()));
		if (CGRect.containsPoint(btn_ok.getBoundingBox(), point)) {
			btn_ok.runAction(CCScaleTo.action(0.08f, 0.95f * App.scaleX));
		} else if (CGRect.containsPoint(btn_exit.getBoundingBox(), point)) {
			btn_exit.runAction(CCScaleTo.action(0.08f, 0.95f * App.scaleX));
		} else {
			btn_ok.stopAllActions();
			btn_exit.stopAllActions();
			btn_ok.setScale(App.scaleX);
			btn_exit.setScale(App.scaleX);
		}
		return super.ccTouchesMoved(event);
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint point = CCDirector.sharedDirector().convertToGL(
				CGPoint.ccp(event.getX(), event.getY()));
		if (CGRect.containsPoint(btn_ok.getBoundingBox(), point)) {
			btn_ok.runAction(CCScaleTo.action(0.08f, App.scaleX));
			onOkClick();
		} else if (CGRect.containsPoint(btn_exit.getBoundingBox(), point)) {
			btn_exit.runAction(CCScaleTo.action(0.08f, App.scaleX));
			onExitClick();
		}
		return super.ccTouchesEnded(event);
	}

	private void showToast(String str) {
		CCSprite bg = CCSprite.sprite("toast.png");
		bg.setPosition(screenSize.width / 2, screenSize.height / 2);
		bg.setOpacity(0);
		CCLabel label = CCLabel.makeLabel(str, "Arial", 40 * App.scaleY / 2.5f);
		label.setColor(ccColor3B.ccWHITE);
		label.setPosition(screenSize.width / 2, screenSize.height / 2);
		label.setOpacity(0);
		bg.setScaleX((label.getContentSize().width + 30 * App.scaleX) / 30);
		bg.setScaleY((label.getContentSize().height + 10 * App.scaleY) / 20);
		this.addChild(bg);
		this.addChild(label);
		label.runAction(CCSequence.actions(CCFadeIn.action(0.5f),
				CCDelayTime.action(1.6f), CCFadeOut.action(0.5f)));
		bg.runAction(CCSequence.actions(CCFadeIn.action(0.5f),
				CCDelayTime.action(1.6f), CCFadeOut.action(0.5f)));
	}

}
