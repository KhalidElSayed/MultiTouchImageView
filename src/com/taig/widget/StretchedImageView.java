package com.taig.widget;

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
		int width = MeasureSpec.getSize( widthMeasureSpec );
		int height = MeasureSpec.getSize( heightMeasureSpec );
		setMeasuredDimension( width, height );

		if( getDrawable() != null && getImageMatrix().isIdentity() )
		{
			// Scale image to match parent.
			float scale = getInitialScale( width, height );
			getImageMatrix().postScale( scale, scale, 0, 0 );
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