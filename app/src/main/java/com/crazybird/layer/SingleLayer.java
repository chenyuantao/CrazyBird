package com.crazybird.layer;

import java.util.ArrayList;

import org.apache.http.Header;
import org.cocos2d.actions.UpdateCallback;
import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.ease.CCEaseIn;
import org.cocos2d.actions.ease.CCEaseOut;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCMoveBy;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCRotateTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.actions.interval.CCSpawn;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.nodes.CCLabelAtlas;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;

import android.view.MotionEvent;
import com.crazybird.App;
import com.crazybird.scene.WhichScene;
import com.crazybird.service.OnCollisionListener;
import com.crazybird.sprite.Banana;
import com.crazybird.sprite.Monster;
import com.crazybird.sprite.Pokeball;
import com.crazybird.sprite.Yellow;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

public class SingleLayer extends CCLayer {
	private CGSize screenSize;
	private CCSprite bird; // 48 * 48
	private CCSprite ready;
	private CCSprite gameover;
	private CCSprite white;
	private CCSprite start;
	private CCSprite back;
	private ArrayList<CCSprite> medals;
	private boolean canFly;
	private float time;
	private int score;
	private ArrayList<OnCollisionListener> list;
	private boolean canTouch;
	private Monster monster;
	private float v0;

	public SingleLayer() {
		getGlobal();
		screenSize = CCDirector.sharedDirector().winSize();
		list = new ArrayList<OnCollisionListener>();
		medals = new ArrayList<CCSprite>();
		initActions();
		bird = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("bird0.png"));
		bird.setScaleX(App.scaleX);
		bird.setScaleY(App.scaleY);

		ready = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("ready.png"));
		ready.setScaleX(App.scaleX);
		ready.setScaleY(App.scaleY);

		start = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("start.png"));
		start.setScaleX(App.scaleX);
		start.setScaleY(App.scaleY);
		start.setPosition(screenSize.width * 0.5f - 64 * App.scaleX, (112 + 39)
				* App.scaleY);

		back = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("back.png"));
		back.setScaleX(App.scaleX);
		back.setScaleY(App.scaleY);
		back.setPosition(screenSize.width * 0.5f + 64 * App.scaleX, (112 + 39)
				* App.scaleY);

		gameover = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("gameover_score.png"));
		gameover.setScaleX(App.scaleX);
		gameover.setScaleY(App.scaleY);
		gameover.setPosition(screenSize.width * 0.5f, 296 * App.scaleY);

		white = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("white.png"));
		white.setScaleX(screenSize.width / 32);
		white.setScaleY(screenSize.height / 32);
		white.setPosition(screenSize.width / 2, screenSize.height / 2);
		white.setOpacity(0);

		this.addChild(bird);
		this.addChild(ready);
		this.addChild(gameover);
		this.addChild(start);
		this.addChild(back);
		for (int i = 0; i < 3; i++) {
			CCSprite medal = CCSprite.sprite(CCSpriteFrameCache
					.sharedSpriteFrameCache().getSpriteFrame(
							"medal" + i + ".png"));
			medal.setScaleX(App.scaleX);
			medal.setScaleY(App.scaleY);
			medal.setPosition(screenSize.width * 0.5f - 64 * App.scaleX,
					260 * App.scaleY);
			medal.setVisible(false);
			this.addChild(medal);
			medals.add(medal);
		}
		this.addChild(white);
		this.setIsTouchEnabled(true);
		this.schedule(new UpdateCallback() {

			@Override
			public void update(float arg0) {
				if (bird.getPosition().y < (112 + 15f) * App.scaleY) {
					bird.setPosition(90 * App.scaleX, (112 + 15f) * App.scaleY);
					gameover();
					return;
				}
				if (bird.getPosition().y > screenSize.height - 6 * App.scaleY) {
					canFly = false;
				} else {
					canFly = true;
				}
				if (time <= 0f) {
					return;
				}
				time = time + 0.01f;
				showScore((int) time);
				// FIXME
				bird.setPosition(bird.getPosition().x, bird.getPosition().y
						+ v0 * 0.01f - 4000 * 0.5f * 0.01f * 0.01f * App.scaleY);
				v0 = v0 - 5600 * 0.01f * App.scaleY;
				if (monster == null) {
					switch (App.which) {
					case App.BANANA:
						monster = new Banana();
						break;
					case App.POKEBALL:
						monster = new Pokeball();
						break;
					case App.YELLOW:
						monster = new Yellow();
						break;
					default:
						monster = new Banana();
						break;
					}
				}
				// 碰撞检测接口
				if (list.size() != 0) {
					for (OnCollisionListener listener : list) {
						if (listener != null) {
							listener.OnCollision(bird);
						}
					}
				}
			}

		}, 0.001f);
		score = -1;
		ready();
	}

	public void gameover() {
		App.playRaw(App.RAW_DEAD);
		v0 = 0f;
		canTouch = false;
		score = (int) time;
		time = 0f;
		monster.destory();
		monster = null;
		bird.stopAllActions();
		white.runAction(CCSequence.actions(CCFadeIn.action(0.12f),
				CCFadeOut.action(0.12f),
				CCCallFuncN.action(this, "callGameover"))); // blink之后转到callGameover里面
	}

	public void callGameover(Object sender) {
		CCNode now = this.getChildByTag(score);
		if (now != null) {
			now.runAction(CCMoveBy.action(0.5f,
					CGPoint.ccp(0, -(now.getPosition().y - 250 * App.scaleY))));
		}
		gameover.setVisible(true);
		gameover.setOpacity(0);
		start.setVisible(true);
		start.setOpacity(0);
		back.setVisible(true);
		back.setOpacity(0);
		medals.get(App.which).setVisible(true);
		medals.get(App.which).setOpacity(0);
		gameover.runAction(CCFadeIn.action(0.8f));
		start.runAction(CCFadeIn.action(0.8f));
		back.runAction(CCFadeIn.action(0.8f));
		showBest();
		showGlobal();
		medals.get(App.which).runAction(CCFadeIn.action(0.8f));
		canTouch = true;
	}

	private void ready() {
		App.playRaw(App.RAW_SHOW);
		if (best != null) {
			best.setVisible(false);
		}
		if (global != null) {
			global.setVisible(false);
		}
		gameover.setVisible(false);
		start.setVisible(false);
		back.setVisible(false);
		for (CCSprite medal : medals) {
			medal.setVisible(false);
		}

		if (score > 0) {
			this.removeChildByTag(score, true);
			this.removeChildByTag(score - 1, true);
		} else {
			this.removeChildByTag(0, true);
		}
		bird.setRotation(0f);
		bird.stopAllActions();
		bird.runAction(repeatFly);
		bird.runAction(repeatUpDown);
		bird.setPosition(90 * App.scaleX, 262 * App.scaleY); // 离地 150像素
		ready.setPosition(screenSize.width * 0.5f, 296 * App.scaleY);
		ready.setOpacity(0);
		ready.setVisible(true);
		ready.runAction(fadeIn);
		time = 0f;
		score = -1;
		canFly = true;
		canTouch = true;
		showScore(0);
	}

	private CCRepeatForever repeatFly;
	private CCRepeatForever repeatUpDown;
	private CCSequence seqUp;
	private CCSequence seqRotate;
	private CCEaseIn easeRotateDown;
	private CCFadeIn fadeIn;
	private CCSequence seqOut;

	private void initActions() {
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 3; i++) {
			frames.add(CCSpriteFrameCache.sharedSpriteFrameCache()
					.getSpriteFrame("bird" + i + ".png"));
		}
		repeatFly = CCRepeatForever.action(CCAnimate.action(CCAnimation
				.animation("bird", 0.13f, frames)));
		CCRotateTo rotateUp = CCRotateTo.action(0.3f, -30);
		CCRotateTo rotateDown = CCRotateTo.action(0.6f, 90);
		CCMoveBy moveUp = CCMoveBy.action(0.18f,
				CGPoint.ccp(0, 40 * App.scaleY));
		CCMoveBy moveDown = CCMoveBy.action(1.1f,
				CGPoint.ccp(0, -424 * App.scaleY));
		easeRotateDown = CCEaseIn.action(rotateDown, 2f);

		CCEaseIn easeMoveDown = CCEaseIn.action(moveDown, 2f);
		CCEaseOut easeRotateUp = CCEaseOut.action(rotateUp, 3f);
		CCSpawn spawnUp = CCSpawn.actions(easeRotateUp, moveUp);
		CCSpawn spawnDown = CCSpawn.actions(easeRotateDown, easeMoveDown);
		seqUp = CCSequence.actions(spawnUp, spawnDown);
		seqRotate = CCSequence.actions(easeRotateUp, easeRotateDown);

		repeatUpDown = CCRepeatForever.action(CCSequence.actions(
				CCMoveTo.action(0.4f,
						CGPoint.ccp(90 * App.scaleX, 265 * App.scaleY)),
				CCMoveTo.action(0.4f,
						CGPoint.ccp(90 * App.scaleX, 259 * App.scaleY))));

		CCFadeOut fadeOut = CCFadeOut.action(0.3f);
		fadeIn = CCFadeIn.action(0.8f);
		seqOut = CCSequence.actions(fadeOut,
				CCCallFuncN.action(this, "onFadeOut"));
	}

	public void onFadeOut(Object sender) {
		CCSprite s = (CCSprite) sender;
		s.setVisible(false);
	}

	private void showScore(int score) {
		CCNode last = this.getChildByTag(score - 1);
		CCNode now = this.getChildByTag(score);
		if (last != null && score > 0) {
			last.setVisible(false);
			last.removeSelf();
		}
		if (now == null) {
			CCLabelAtlas a = CCLabelAtlas.label("" + score, "score.png", 26,
					44, '0');
			if (score < 10) {
				a.setPosition(screenSize.width * 0.5f - 13 * App.scaleX,
						420 * App.scaleY);
			} else if (score < 100) {
				a.setPosition(screenSize.width * 0.5f - 26 * App.scaleX,
						420 * App.scaleY);
			} else if (score < 1000) {
				a.setPosition(screenSize.width * 0.5f - 39 * App.scaleX,
						420 * App.scaleY);
			}
			a.setScaleX(App.scaleX);
			a.setScaleY(App.scaleY);
			a.setTag(score);
			if (score != 0) {
				App.playRaw(App.RAW_SCORE);
			}
			this.addChild(a);
		}
	}

	private CCLabelAtlas best;
	private CCLabelAtlas global;

	private void showBest() {
		if (best != null) {
			best.removeSelf();
		}
		int mScore = App.getBest();
		if (this.score > mScore) {
			mScore = this.score;
			App.saveBest(mScore);
		}
		best = CCLabelAtlas.label("" + mScore, "number.png", 12, 14, '0');
		best.setAnchorPoint(1f, 0.5f);
		best.setPosition(217 * App.scaleX, 275 * App.scaleY);
		best.setScaleX(App.scaleX);
		best.setScaleY(App.scaleY);
		this.addChild(best);
		best.runAction(CCFadeIn.action(0.8f));
	}

	private void showGlobal() {
		if (global != null) {
			global.removeSelf();
		}
		if (this.score > App.global&&App.global!=-1) {
			App.global = this.score;
			setGlobal();
		}
		global = CCLabelAtlas.label("" + (App.global==-1?0:App.global), "number.png", 12, 14, '0');
		global.setAnchorPoint(1f, 0.5f);
		global.setPosition(217 * App.scaleX, 225 * App.scaleY);
		global.setScaleX(App.scaleX);
		global.setScaleY(App.scaleY);
		this.addChild(global);
		global.runAction(CCFadeIn.action(0.8f));
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		if (!canTouch) {
			return super.ccTouchesBegan(event);
		}

		if (score > -1) {
			CGPoint point = CCDirector.sharedDirector().convertToGL(
					CGPoint.ccp(event.getX(), event.getY()));
			if (start.getBoundingBox().contains(point.x, point.y)) {
				ready();
			}
			if (back.getBoundingBox().contains(point.x, point.y)) {
				home();
			}

			return super.ccTouchesBegan(event);
		}
		if (canFly) {
			// FIXME
			bird.stopAction(seqRotate);
			bird.runAction(seqRotate);
			v0 = 700f * App.scaleY;
			App.playRaw(App.RAW_FLY);
			// bird.stopAction(seqUp);
			// bird.runAction(seqUp);
		}
		// 开始游戏
		if (time == 0f) {
			time = 0.01f;
			bird.stopAction(repeatUpDown);
			ready.runAction(seqOut);
		}

		return super.ccTouchesBegan(event);
	}

	private void home() {
		App.playRaw(App.RAW_SHOW);
		TransitionLayer layer = (TransitionLayer) App.singleScene
				.getChildByTag((10100));
		App.whichScene = new WhichScene();
		layer.transition(App.singleScene, App.whichScene);
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		return super.ccTouchesEnded(event);
	}

	public void addListener(OnCollisionListener listener) {
		list.add(listener);
	}

	public void removeListener(OnCollisionListener listener) {
		list.remove(listener);
	}

	private void getGlobal() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				SyncHttpClient client = new SyncHttpClient();
				App.global = -1;
				client.get(
						"http://www.crazybird2016.applinzi.com/getGlobalScore.php?id="
								+ (App.which + 1),
						new TextHttpResponseHandler() {

							@Override
							public void onFailure(int arg0, Header[] arg1,
									String arg2, Throwable arg3) {
								App.global = -1;
							}

							@Override
							public void onSuccess(int arg0, Header[] arg1,
									String str) {
								String result = "";
								for (int i = 0, len = str.length(); i < len; i++) {
									if (Character.isDigit(str.charAt(i))) {
										result += str.charAt(i);
									} else {
										break;
									}
								}
								if (!result.isEmpty()) {
									App.global = Integer.valueOf(result);
								} else {
									App.global = -1;
								}
							}
						});
			}
		});
		thread.run();
	}

	private void setGlobal() {
		if(App.global==-1){
			return;
		}		
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				SyncHttpClient client = new SyncHttpClient();
				client.get(
						"http://www.crazybird2016.applinzi.com/setGlobalScore.php?id="
								+ (App.which + 1) + "&score=" + App.global,
						new TextHttpResponseHandler() {

							@Override
							public void onFailure(int arg0, Header[] arg1,
									String arg2, Throwable arg3) {
								showToast("未能连接服务器，上传最高分失败。");
							}

							@Override
							public void onSuccess(int arg0, Header[] arg1,
									String str) {
								if (str.contains("FAILED")) {
									str = str.replace("FAILED", "");
									String result = "";
									for (int i = 0, len = str.length(); i < len; i++) {
										if (Character.isDigit(str.charAt(i))) {
											result += str.charAt(i);
										} else {
											break;
										}
									}
									if (!result.isEmpty()) {
										App.global = Integer.valueOf(result);
										showToast("少侠来晚了，目前最高分为" + App.global);
									} else {
										showToast("未知原因，上传最高分失败");
									}

								} else if (str.contains("SUCCESS")) {
									showToast("恭喜，最高分" + App.global + "分由你所创！");
								}
							}
						});
			}
		});
		thread.run();
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
				CCDelayTime.action(2f), CCFadeOut.action(0.5f)));
		bg.runAction(CCSequence.actions(CCFadeIn.action(0.5f),
				CCDelayTime.action(2f), CCFadeOut.action(0.5f)));
	}

}
