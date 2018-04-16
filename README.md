# viewslipe
1：此项目实现一个 viewpager + 滑动删除

2：改写viewpager解决滑动冲突问题。CstViewPager。

3：继承viewgroup实现滑动删除功能SwipeMenuLayout。

	重点解析这个类。
1:onMeasure
	实现了  1：解决viewholder的复用机制，赋初值
		2：获取布局属性MarginLayoutParams
		3：计算view的宽度 mRightMenuWidths += childView.getMeasuredWidth();
		4：设置view的大小实现view的测量。
2：onLayout

	左滑：childView.layout(left, getPaddingTop(), left + childView.getMeasuredWidth(), getPaddingTop() + childView.getMeasuredHeight());
              left = left + childView.getMeasuredWidth();

	右滑：childView.layout(right - childView.getMeasuredWidth(), getPaddingTop(), right, getPaddingTop() + childView.getMeasuredHeight());
              right = right - childView.getMeasuredWidth();

	也就是说这个布局是自定义的。可以根据不同的需求随意改。


3：dispatchTouchEvent
	1：ACTION_DOWN：
			1：这里配合move解决阻塞式交互
			   iosInterceptFlag = false;
			if (mViewCache != null) {
                        	if (mViewCache != this) {
                           	 mViewCache.smoothClose();
                            	//这里每次都会交互式的进行。
                            	iosInterceptFlag = isIos;
                        	}
                        	getParent().requestDisallowInterceptTouchEvent(true);
                    	}
			在ACTION_MOVE:
			if (iosInterceptFlag) {
                        	break;//先后的互斥。
                   	}
			这样就实现了阻塞式交互。因为每次动作都是从down开始的。（同一个View就可以随便滑动，不同的就进入阻塞式了）

	2：ACTION_MOVE:
			1：if (Math.abs(gap) > 10 || Math.abs(getScrollX()) > 10) {
                        	getParent().requestDisallowInterceptTouchEvent(true);
                    	}
			这个滑动事件属于侧滑删除控件的。此处屏蔽viewGroup的反应。

			2：打开滑动标志位
			   if (Math.abs(gap) > mScaleTouchSlop) {
                        	isUnMoved = false;
                    		}
			3：开始滑动 scrollBy((int) (gap), 0);

			4：越界修正。
				if (getScrollX() < 0) {
                            		scrollTo(0, 0);
                        	}
                        	if (getScrollX() > mRightMenuWidths) {
                            		scrollTo(mRightMenuWidths, 0);
                        	}
			5：记录最后滑动点 mLastP.set(ev.getRawX(), ev.getRawY());


	3：ACTION_UP：
			1：置滑动标志位
				if (Math.abs(ev.getRawX() - mFirstP.x) > mScaleTouchSlop) {
                        		isUserSwiped = true;
                   		 }
			  在拦截方法里
				if (isUserSwiped) {
                        		return true;
                    		}
			  也就是是说在移动状态下屏蔽一切点击事件。
			2：然后就是左滑右滑动画部分。
			3：释放touch标志  
				isTouching = false;
			   不要忘了释放速度检测releaseVelocityTracker();

4：消息的拦截机制：
			
	1：ACTION_MOVE：
			1：滑动时屏蔽点击等事件
			   
                    	if (Math.abs(ev.getRawX() - mFirstP.x) > mScaleTouchSlop) {
                        	return true;
                    	}
	2：ACTION_UP   
			if (isLeftSwipe) {
                        if (getScrollX() > mScaleTouchSlop) {
                    
                            if (ev.getX() < getWidth() - getScrollX()) {
                                if (isUnMoved) {
                                    smoothClose();
                                }
                                return true;//true表示拦截
                            }
                        }
                    	}
			这里的意思是左滑成立，但是点击的位置在滑动展开部分的左侧，所以要拦截。











