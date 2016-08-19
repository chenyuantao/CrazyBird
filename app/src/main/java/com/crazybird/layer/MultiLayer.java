package com.crazybird.layer;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.cocos2d.actions.CCProgressTimer;
import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.ease.CCEaseIn;
import org.cocos2d.actions.ease.CCEaseOut;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateBy;
import org.cocos2d.actions.interval.CCRotateTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabelAtlas;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.content.Context;
import android.renderscript.Float2;
import android.view.MotionEvent;

import com.crazybird.App;
import com.crazybird.bluetooth.Bluetooth;
import com.crazybird.scene.CharacterScene;
import com.crazybird.scene.MultiScene;
import com.crazybird.service.OnCollisionListener;
import com.crazybird.sprite.Banana;
import com.crazybird.sprite.Monster;
import com.crazybird.sprite.Pokeball;
import com.crazybird.sprite.Yellow;

public class MultiLayer extends CCLayer {
	private CGSize screenSize;
	private Context mContext;
	public boolean isBird;
	private float time;
	private boolean canFly;
	private boolean isWin;
	private boolean canTouch;
	private int which;
	private float v0;
	private CCSprite bird;
	private CCSprite ready;
	private CCSprite white;
	private CCSprite start;
	private CCSprite back;
	private CCSprite gameover;
	private CCLabelAtlas label;
	private ArrayList<CCSprite> skills;
	private ArrayList<CCProgressTimer> masks;
	private ArrayList<Integer> percentages;
	private ArrayList<CCSpriteFrame> framesFly;
	private ArrayList<OnCollisionListener> list;
	private CCSequence seqRotate;

	private DecimalFormat decimalFormat = new DecimalFormat(".00");

	public MultiLayer(boolean isBird, Context context) {
		App.playRaw(App.RAW_SHOW);
		this.isBird = isBird;
		mContext = context;
		list = new ArrayList<OnCollisionListener>();
		screenSize = CCDirector.sharedDirector().winSize();
		framesFly = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 3; i++) {
			framesFly.add(CCSpriteFrameCache.sharedSpriteFrameCache()
					.getSpriteFrame("bird" + i + ".png"));
		}

		bird = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("bird0.png"));
		bird.setScaleX(App.scaleX);
		bird.setScaleY(App.scaleY);
		this.addChild(bird);

		ready = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("ready.png"));
		ready.setScaleX(App.scaleX);
		ready.setScaleY(App.scaleY);
		ready.setPosition(screenSize.width * 0.5f, 296 * App.scaleY);
		ready.setOpacity(0);
		this.addChild(ready);

		start = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("start.png"));
		start.setScaleX(App.scaleX);
		start.setScaleY(App.scaleY);
		start.setPosition(screenSize.width * 0.5f - 64 * App.scaleX, (112 + 39)
				* App.scaleY);
		start.setOpacity(0);
		this.addChild(start);

		back = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("back.png"));
		back.setScaleX(App.scaleX);
		back.setScaleY(App.scaleY);
		back.setPosition(screenSize.width * 0.5f + 64 * App.scaleX, (112 + 39)
				* App.scaleY);
		back.setOpacity(0);
		this.addChild(back);

		gameover = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("win.png"));
		gameover.setScaleX(App.scaleX);
		gameover.setScaleY(App.scaleY);
		gameover.setPosition(screenSize.width * 0.5f, 296 * App.scaleY);
		gameover.setOpacity(0);
		this.addChild(gameover);

		label = CCLabelAtlas.label("10", "score.png", 26, 44, '0');
		label.setScaleX(App.scaleX);
		label.setScaleY(App.scaleY);
		label.setAnchorPoint(0.5f, 0f);
		label.setPosition(screenSize.width * 0.5f, 420 * App.scaleY);
		this.addChild(label);

		CCRotateTo rotateDown = CCRotateTo.action(0.6f, 90);
		CCRotateTo rotateUp = CCRotateTo.action(0.3f, -30);
		CCEaseIn easeRotateDown = CCEaseIn.action(rotateDown, 2f);
		CCEaseOut easeRotateUp = CCEaseOut.action(rotateUp, 3f);
		seqRotate = CCSequence.actions(easeRotateUp, easeRotateDown);

		skills = new ArrayList<CCSprite>();
		masks = new ArrayList<CCProgressTimer>();
		percentages = new ArrayList<Integer>();
		for (int i = 0; i < 3; i++) {
			// 添加技能条
			CCSprite skill = CCSprite.sprite(CCSpriteFrameCache
					.sharedSpriteFrameCache().getSpriteFrame(
							"skill" + i + ".png"));
			skill.setScaleX(App.scaleX);
			skill.setScaleY(App.scaleY);
			skill.setPosition(screenSize.width - 22.5f * App.scaleX - 45f
					* App.scaleX * i, 24.5f * App.scaleY);
			if (isBird) {
				skill.setVisible(false);
			}
			skill.setTag(i);
			this.addChild(skill);
			skills.add(skill);

			// 添加阴影条
			CCProgressTimer mask = CCProgressTimer.progress("mask.png");

			mask.setScaleX(App.scaleX);
			mask.setScaleY(App.scaleY);
			mask.setPosition(screenSize.width - 22.5f * App.scaleX - 45f
					* App.scaleX * i, 24.5f * App.scaleY);
			mask.setType(CCProgressTimer.kCCProgressTimerTypeRadialCCW);
			mask.setPercentage(0f);
			this.addChild(mask);
			masks.add(mask);

			// 添加技能百分比
			percentages.add(0);
		}

		white = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("white.png"));
		white.setScaleX(screenSize.width / 32);
		white.setScaleY(screenSize.height / 32);
		white.setPosition(screenSize.width / 2, screenSize.height / 2);
		white.setOpacity(0);
		this.addChild(white);

		if (isBird) {
			this.schedule(new UpdateCallback() {

				@Override
				public void update(float arg0) {
					if (bird.getPosition().y < (112 + 15f) * App.scaleY) {
						bird.setPosition(90 * App.scaleX, (112 + 15f)
								* App.scaleY);
						Bluetooth.sendMessageHandle("<command,game|b|"
								+ decimalFormat.format(bird.getPosition().x
										/ App.scaleX)
								+ "|"
								+ decimalFormat.format(bird.getPosition().y
										/ App.scaleY) + "|"
								+ decimalFormat.format(bird.getRotation())
								+ ">,");
						Bluetooth
								.sendMessageHandle("<command,game|over|true>,");
						gameover(false);
						return;
					}
					if (bird.getPosition().y > screenSize.height - 6
							* App.scaleY) {
						canFly = false;
					} else {
						canFly = true;
					}
					if (time >= 10f) {
						return;
					}
					// FIXME
					bird.setPosition(bird.getPosition().x, bird.getPosition().y
							+ v0 * 0.01f - 4000 * 0.5f * 0.01f * 0.01f
							* App.scaleY);
					v0 = v0 - 5600 * 0.01f * App.scaleY;
					Bluetooth.sendMessageHandle("<command,game|b|"
							+ decimalFormat.format(bird.getPosition().x / App.scaleX) + "|"
							+ bird.getPosition().y / App.scaleY + "|"
							+ decimalFormat.format(bird.getRotation()) + ">,");
					int last = floatToInt(time);
					time = time - 0.01f;
					int now = floatToInt(time);
					if (last != now) {
						label.setString(now + "");
						Bluetooth.sendMessageHandle("<command,game|t|" + now
								+ ">,");
					}
					if (now == 0) {
						Bluetooth
								.sendMessageHandle("<command,game|over|false>,");
						gameover(true);
						return;
					}
					// 碰撞检测接口
					if (list.size() != 0 && list != null) {
						for (OnCollisionListener listener : list) {
							if (listener != null) {
								listener.OnCollision(bird);
							}
						}
					}
				}
			}, 0.001f);
		} else {
			this.schedule(new UpdateCallback() {

				@Override
				public void update(float arg0) {
					// 技能百分比
					for (int i = 0; i < 3; i++) {
						int percentage = percentages.get(i);
						if (percentage != 0) {
							System.out.println(percentage);
							percentage = percentage - 1;
							percentages.set(i, percentage);
							masks.get(i).setPercentage(percentage);
						}
					}
				}
			}, 0.001f);
		}
		// 小鸟归位
		bird.setRotation(0f);
		bird.stopAllActions();
		bird.runAction(CCRepeatForever.action(CCAnimate.action(CCAnimation
				.animation("bird", 0.13f, framesFly))));
		bird.setPosition(90 * App.scaleX, 262 * App.scaleY);
		// 显示提示
		ready.runAction(CCFadeIn.action(0.8f));
		// 计时器归位
		time = 10f;
		label.setString((int) time + "");
		// 可以飞
		canFly = true;
		// 可以触摸
		canTouch = true;
		which = -1;
		v0 = 0f;
		this.setIsTouchEnabled(true);
	}

	private void start() {
		time = 9.99f;
		ready.stopAllActions();
		ready.runAction(CCFadeOut.action(0.3f));
	}

	private void restart() {
		start.setOpacity(0);
		back.setOpacity(0);
		TransitionLayer t = (TransitionLayer) App.multiScene
				.getChildByTag(10100);
		MultiScene now = App.multiScene;
		App.multiScene = new MultiScene(isBird, mContext);
		t.transition(now, App.multiScene);
	}

	private void back() {
		TransitionLayer t = (TransitionLayer) App.multiScene
				.getChildByTag(10100);
		App.characterScene = new CharacterScene(Bluetooth.isClient, mContext);
		t.transition(App.multiScene, App.characterScene);
	}

	private void shoot(float height, int which) {
		Monster monster = null;
		switch (which) {
		case App.BANANA:
			monster = new Banana(height);
			break;
		case App.POKEBALL:
			monster = new Pokeball(height);
			break;
		case App.YELLOW:
			monster = new Yellow(height, bird.getPosition().x,
					bird.getPosition().y);
			break;
		default:
			monster = new Banana(height);
			break;
		}
	}

	public void gameover(boolean isWin) {
		this.isWin = isWin;
		canTouch = false;
		time = 12f;
		v0 = 0f;
		bird.stopAllActions();
		if (isWin) {
			if (isBird) {
				// 小鸟方胜利
				App.playRaw(App.RAW_WIN);
				this.removeChild(bird, false);
				this.addChild(bird);
				bird.runAction(CCSequence.actions(CCMoveTo.action(0.5f, CGPoint
						.ccp(screenSize.width / 2, screenSize.height / 2)),
						CCRotateTo.action(0.5f, 360f)));
			} else {
				// KILLER方胜利
				App.playRaw(App.RAW_DEAD);
				for (int i = 0; i < 3; i++) {
					masks.get(i).setVisible(false);
					CCSprite skill = skills.get(i);
					skill.stopAllActions();
					skill.setVisible(true);
					skill.runAction(CCMoveTo.action(0.5f, CGPoint.ccp(
							screenSize.width / 2 + 45f * App.scaleX - 45f
									* App.scaleX * i, screenSize.height / 2)));
				}
				App.playRaw(App.RAW_WIN);
			}
		} else {
			if (!isBird) {
				// Killer方失败
				App.playRaw(App.RAW_LOSE);
				this.removeChild(bird, false);
				this.addChild(bird);
				bird.runAction(CCSequence.actions(CCMoveTo.action(0.5f, CGPoint
						.ccp(screenSize.width / 2, screenSize.height / 2)),
						CCRotateTo.action(0.5f, 360f)));
			} else {
				// 小鸟方失败
				App.playRaw(App.RAW_DEAD);
				for (int i = 0; i < 3; i++) {
					masks.get(i).setVisible(false);
					CCSprite skill = skills.get(i);
					skill.stopAllActions();
					skill.setVisible(true);
					skill.runAction(CCMoveTo.action(0.5f, CGPoint.ccp(
							screenSize.width / 2 + 45f * App.scaleX - 45f
									* App.scaleX * i, screenSize.height / 2)));
				}
				App.playRaw(App.RAW_LOSE);
			}
		}
		white.runAction(CCSequence.actions(CCFadeIn.action(0.12f),
				CCFadeOut.action(0.12f),
				CCCallFuncN.action(this, "callGameover"))); // blink之后转到callGameover里面
	}

	public void callGameover(Object sender) {
		if (isWin) {
			gameover.setDisplayFrame(CCSpriteFrameCache
					.sharedSpriteFrameCache().spriteFrameByName("win.png"));
		} else {
			gameover.setDisplayFrame(CCSpriteFrameCache
					.sharedSpriteFrameCache().spriteFrameByName("lose.png"));
		}
		gameover.runAction(CCFadeIn.action(0.8f));
		start.runAction(CCFadeIn.action(0.8f));
		back.runAction(CCFadeIn.action(0.8f));
		canTouch = true;
	}

	public void onReceive(String command) {
		String str[] = command.split("\\|");
		switch (str[0]) {
		case "start":
			start();
			break;
		case "b":
			if (str.length < 4) {
				break;
			}
			bird.setPosition(Float.parseFloat(str[1]) * App.scaleX,
					Float.parseFloat(str[2]) * App.scaleY);
			bird.setRotation(Float.valueOf(str[3]));
			break;
		case "t":
			label.setString(str[1]);
			break;
		case "restart":
			restart();
			break;
		case "back":
			back();
			break;
		case "over":
			if (str[1].equals("true")) {
				gameover(true);
			} else {
				gameover(false);
			}
			break;
		case "m":
			float height = Float.parseFloat(str[1]) * App.scaleY;
			int which = Integer.valueOf(str[2]);
			shoot(height, which);
			break;
		case "fly":
			App.playRaw(App.RAW_FLY);
			break;
		}
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		if (!canTouch) {
			return super.ccTouchesBegan(event);
		}
		CGPoint point = CCDirector.sharedDirector().convertToGL(
				CGPoint.ccp(event.getX(), event.getY()));
		if (start.getOpacity() != 0
				&& start.getBoundingBox().contains(point.x, point.y)) {
			Bluetooth.sendMessageHandle("<command,game|restart>,");
			restart();
			return super.ccTouchesBegan(event);
		}
		if (back.getOpacity() != 0
				&& back.getBoundingBox().contains(point.x, point.y)) {
			Bluetooth.sendMessageHandle("<command,game|back>,");
			back();
			return super.ccTouchesBegan(event);
		}
		if (time > 10f) {
			return super.ccTouchesBegan(event);
		}
		if (isBird) {
			// 开始游戏
			if (time == 10f) {
				Bluetooth.sendMessageHandle("<command,game|start>,");
				start();
			}
			if (canFly && isBird) {
				Bluetooth.sendMessageHandle("<command,game|fly>,");
				App.playRaw(App.RAW_FLY);
				v0 = 700 * App.scaleY;
				bird.stopAction(seqRotate);
				bird.runAction(seqRotate);
			}
		} else {
			if (time < 10f) {
				for (int i = 0; i < 3; i++) {
					CCSprite skill = skills.get(i);
					if (skill.getBoundingBox().contains(point.x, point.y)) {
						if (masks.get(i).getPercentage() == 0f) {
							// 不在CD中
							which = i;
							skill.setPosition(point);
						}
					}
				}
			}
		}

		return super.ccTouchesBegan(event);
	}

	@Override
	public boolean ccTouchesMoved(MotionEvent event) {
		if (isBird || time >= 10f || which == -1) {
			return super.ccTouchesMoved(event);
		}
		CGPoint point = CCDirector.sharedDirector().convertToGL(
				CGPoint.ccp(event.getX(), event.getY()));
		skills.get(which).setPosition(point);
		return super.ccTouchesMoved(event);
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		if (isBird || time >= 10f || which == -1) {
			return super.ccTouchesMoved(event);
		}
		CGPoint base = CGPoint.ccp(screenSize.width - 22.5f * App.scaleX - 45f
				* App.scaleX * which, 24.5f * App.scaleY);
		CGPoint point = CCDirector.sharedDirector().convertToGL(
				CGPoint.ccp(event.getX(), event.getY()));
		Bluetooth.sendMessageHandle("<command,game|m|"
				+ floatToInt(point.y / App.scaleY) + "|" + which + ">,");
		shoot(point.y, which);
		skills.get(which).runAction(
				CCSequence.actions(CCFadeOut.action(0.3f),
						CCMoveTo.action(0.01f, base), CCFadeIn.action(0.3f),
						CCCallFuncN.action(this, "onCD")));
		for (int k = 0; k < 3; k++) {
			if (k != which) {
				percentages.set(k, 30);
			}
		}
		which = -1;
		return super.ccTouchesEnded(event);
	}

	public void onCD(Object sender) {
		CCSprite skill = (CCSprite) sender;
		int i = skill.getTag();
		percentages.set(i, 100);
	}

	private int floatToInt(float f) {
		int i = 0;
		if (f > 0) // 正数
			i = (int) ((f * 10 + 5) / 10);
		else if (f < 0) // 负数
			i = (int) ((f * 10 - 5) / 10);
		else
			i = 0;
		return i;

	}

	public void addListener(OnCollisionListener listener) {
		list.add(listener);
	}

	public void removeListener(OnCollisionListener listener) {
		list.remove(listener);
	}

}
