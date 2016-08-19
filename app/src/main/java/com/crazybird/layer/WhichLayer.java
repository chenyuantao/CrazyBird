package com.crazybird.layer;

import java.util.ArrayList;

import org.cocos2d.actions.base.CCRepeatForever;
import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCAnimate;
import org.cocos2d.actions.interval.CCFadeIn;
import org.cocos2d.actions.interval.CCFadeOut;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCScaleTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCAnimation;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.nodes.CCSpriteFrame;
import org.cocos2d.nodes.CCSpriteFrameCache;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;

import com.crazybird.App;
import com.crazybird.BluetoothActivity;
import com.crazybird.scene.SingleScene;

public class WhichLayer extends CCLayer {
	// 0 = Pokeball
	// 1 = Banana
	// 2 = Yellow
	private CGSize screenSize;
	private CCSprite bird;
	private CCSprite title;
	private ArrayList<CCSprite> btns_mode;
	private ArrayList<CCSprite> btns_game;
	private int state;
	private static final int SELECT_MODE = 0;
	private static final int SELECT_GAME = 1;
	private static final int CHANGING = 2;

	public WhichLayer() {
		screenSize = CCDirector.sharedDirector().winSize();
		title = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("title.png"));
		title.setScaleX(App.scaleX);
		title.setScaleY(App.scaleY);
		title.setPosition(screenSize.width / 2, App.scaleY * 380);

		bird = CCSprite.sprite(CCSpriteFrameCache.sharedSpriteFrameCache()
				.getSpriteFrame("bird0.png"));
		bird.setScale(App.scaleX);
		bird.setPosition(screenSize.width / 2, App.scaleY * 310);
		ArrayList<CCSpriteFrame> frames = new ArrayList<CCSpriteFrame>();
		for (int i = 0; i < 3; i++) {
			frames.add(CCSpriteFrameCache.sharedSpriteFrameCache()
					.getSpriteFrame("bird" + i + ".png"));
		}
		bird.runAction(CCRepeatForever.action(CCAnimate.action(CCAnimation
				.animation("bird", 0.13f, frames))));
		bird.runAction(CCRepeatForever.action(CCSequence.actions(
				CCMoveTo.action(0.4f,
						CGPoint.ccp(bird.getPosition().x, 313 * App.scaleY)),
				CCMoveTo.action(0.4f,
						CGPoint.ccp(bird.getPosition().x, 307 * App.scaleY)))));

		btns_mode = new ArrayList<CCSprite>();
		for (int i = 0; i < 3; i++) {
			CCSprite btn_mode = CCSprite.sprite(CCSpriteFrameCache
					.sharedSpriteFrameCache().getSpriteFrame(
							"btn_" + i + ".png"));
			btn_mode.setScale(App.scaleX);
			btn_mode.setPosition(CGPoint.ccp(screenSize.width / 2,
					(250 - 54 * i) * App.scaleY));
			btns_mode.add(btn_mode);
			this.addChild(btn_mode);
		}

		btns_game = new ArrayList<CCSprite>();
		for (int i = 0; i < 4; i++) {
			CCSprite btn_game = CCSprite.sprite(CCSpriteFrameCache
					.sharedSpriteFrameCache().getSpriteFrame(
							"btn_2_" + i + ".png"));
			btn_game.setScale(App.scaleX);
			btn_game.setPosition(CGPoint.ccp(screenSize.width / 2,
					(250 - 54 * i) * App.scaleY));
			btn_game.setOpacity(0);
			btns_game.add(btn_game);
			this.addChild(btn_game);
		}

		this.addChild(title);
		this.addChild(bird);
		this.setIsTouchEnabled(true);
		state = SELECT_MODE;
	}

	@Override
	public boolean ccTouchesBegan(MotionEvent event) {
		CGPoint point = CCDirector.sharedDirector().convertToGL(
				CGPoint.ccp(event.getX(), event.getY()));
		if (state == CHANGING) {
			return super.ccTouchesBegan(event);
		}

		if (state == SELECT_MODE) {
			for (CCSprite btn_mode : btns_mode) {
				if (btn_mode.getBoundingBox().contains(point.x, point.y)) {
					btn_mode.runAction(CCScaleTo.action(0.08f,
							0.95f * App.scaleX));
				} else {
					btn_mode.runAction(CCScaleTo.action(0.08f, App.scaleX));
				}
			}
		}
		if (state == SELECT_GAME) {
			for (CCSprite btn_game : btns_game) {
				if (btn_game.getBoundingBox().contains(point.x, point.y)) {
					btn_game.runAction(CCScaleTo.action(0.08f,
							0.95f * App.scaleX));
				} else {
					btn_game.runAction(CCScaleTo.action(0.08f, App.scaleX));
				}
			}
		}
		return super.ccTouchesBegan(event);
	}

	@Override
	public boolean ccTouchesMoved(MotionEvent event) {
		return super.ccTouchesMoved(event);
	}

	@Override
	public boolean ccTouchesEnded(MotionEvent event) {
		CGPoint point = CCDirector.sharedDirector().convertToGL(
				CGPoint.ccp(event.getX(), event.getY()));
		if(state==CHANGING){
			return super.ccTouchesBegan(event);
		}
		if(state==SELECT_MODE){			
			for(int i=0;i<btns_mode.size();i++){
				CCSprite btn_mode = btns_mode.get(i);
				if(btn_mode.getBoundingBox().contains(point.x,point.y)){
					state=CHANGING;	
					if(i==0){
						//单人模式
						App.playRaw(App.RAW_SCORE);
						btn_mode.runAction(CCSequence.actions(CCScaleTo.action(0.08f, App.scaleX),CCFadeOut.action(0.242f), CCCallFuncN.action(this, "MODE2GAME")));
						for(CCSprite btn_mode_other:btns_mode){
							if(!btn_mode_other.equals(btn_mode)){
								btn_mode_other.runAction(CCFadeOut.action(0.25f));
							}						
						}
					}
					if(i==1){
						//多人模式
						App.playRaw(App.RAW_SCORE);
						state=SELECT_MODE;
						btn_mode.runAction(CCScaleTo.action(0.08f, App.scaleX));
						Intent intent = new Intent();
						intent.setClass(App.context, BluetoothActivity.class);
						App.context.startActivity(intent);						
					}
					if(i==2){
						//退出						
						System.exit(0);
					}
				}
			}
		}		
		if(state==SELECT_GAME){
			for(int i=0;i<btns_game.size();i++){
				CCSprite btn_game = btns_game.get(i);
				if(btn_game.getBoundingBox().contains(point.x,point.y)){
					state=CHANGING;	
					if(i==3){
						//返回
						App.playRaw(App.RAW_SHOW);
						btn_game.runAction(CCSequence.actions(CCScaleTo.action(0.08f, App.scaleX),CCFadeOut.action(0.242f), CCCallFuncN.action(this, "GAME2MODE")));
						for(CCSprite btn_game_other:btns_game){
							if(!btn_game_other.equals(btn_game)){
								btn_game_other.runAction(CCFadeOut.action(0.25f));
							}						
						}						
					}else{
						App.which=i;
						//选择游戏类型
						App.singleScene = new SingleScene();
						TransitionLayer t = (TransitionLayer)App.whichScene.getChildByTag(10100);
						t.transition(App.whichScene, App.singleScene);
					}
				}				
			}			
		}
		return super.ccTouchesEnded(event);	
	}

	public void MODE2GAME(Object sender) {
		state = SELECT_GAME;
		for (CCSprite btn_game : btns_game) {
			btn_game.runAction(CCFadeIn.action(0.25f));
		}
	}

	public void GAME2MODE(Object sender) {
		state = SELECT_MODE;
		for (CCSprite btn_mode : btns_mode) {
			btn_mode.runAction(CCFadeIn.action(0.25f));
		}
	}

}
