package com.taig.multitouch.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

public class StretchedImageView extends ImageView
{
	public StretchedImageView( Context context )
	{
		this( context, null, 0 );
	}

	public StretchedImageView( Context context, AttributeSet attrs )
	{
		this( context, attrs, 0 );
	}

	public StretchedImageView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );

		this.setImageMatrix( new Matrix() );
		this.setScaleType( ScaleType.MATRIX );
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
	{
		if( getDrawable() != null && getImageMatrix().isIdentity() )
		{
			// Scale image to match parent.
			int width = MeasureSpec.getSize( widthMeasureSpec );
			int height = MeasureSpec.getSize( heightMeasureSpec );

			float scale = getInitialScale( width, height );
			getImageMatrix().postScale( scale, scale, 0, 0 );

			setMeasuredDimension( (int) ( getDrawable().getIntrinsicWidth() * scale ), (int) ( getDrawable().getIntrinsicHeight() * scale ) );
		}
		else
		{
			super.onMeasure( widthMeasureSpec, heightMeasureSpec );
		}
	}

	/**
	 * Get the initial scale factor to match the parent's width or height.
	 * 
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	protected float getInitialScale( int viewWidth, int viewHeight )
	{
		return Math.min( viewWidth / (float) getDrawable().getIntrinsicWidth(), viewHeight / (float) getDrawable().getIntrinsicHeight() );
	}
}