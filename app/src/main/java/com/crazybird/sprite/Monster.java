package com.crazybird.sprite;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;

import com.crazybird.App;
import com.crazybird.service.OnCollisionListener;

public abstract class Monster {
	protected CGSize screenSize;
	protected float birdX;
	protected float birdY;
	protected float time;
	protected float height = 0f;
	protected OnCollisionListener listener = new OnCollisionListener() {

		@Override
		public void OnCollision(CCSprite bird) {
			// 判断是否已销毁
			if (time == 0f) {
				return;
			}
			time = time + 0.01f;
			// 判断是否已存在duration秒
			// if ((int) time == App.duration) {
			// destory();
			// return;
			// }
			birdY = bird.getPosition().y;
			birdX = bird.getPosition().x;
			if (height == 0f) {
				onUpdate(bird);
			}

			onCollision(bird);
		}

	};

	protected abstract void onDestory();

	protected abstract void onCollision(CCSprite bird);

	protected abstract void onUpdate(CCSprite bird);

	public Monster() {
		init();		
	}

	private void init() {
		screenSize = CCDirector.sharedDirector().winSize();
		time = 0.01f;
	}

	public void destory() {
		time = 0f;
		onDestory();
	};

	protected CGRect smallRect(CCNode sprite) {
		CGRect bigR = sprite.getBoundingBox();
		CGRect smallR = CGRect.make(bigR.origin.x + 1.5f * App.scaleX,
				bigR.origin.y - 1.5f * App.scaleX, bigR.size.width - 3
						* App.scaleX, bigR.size.height - 3 * App.scaleX);
		return smallR;
	}

}
