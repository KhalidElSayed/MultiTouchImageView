package com.taig.mt;

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

	public StretchedImageView( Context context, AttributeSet attributes )
	{
		this( context, attributes, 0 );
	}

	public StretchedImageView( Context context, AttributeSet attributes, int defStyle )
	{
		super( context, attributes, defStyle );

		this.setImageMatrix( new Matrix() );
		this.setScaleType( ScaleType.MATRIX );
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
	{
		if( getDrawable() != null && getImageMatrix().isIdentity() )
		{
			// Scale image to match parent.
			float scale = getInitialScale( MeasureSpec.getSize( widthMeasureSpec ),
										   getDrawable().getIntrinsicWidth(),
										   MeasureSpec.getSize( heightMeasureSpec ),
										   getDrawable().getIntrinsicHeight() );
			getImageMatrix().postScale( scale, scale, 0, 0 );

			setMeasuredDimension( (int) ( getDrawable().getIntrinsicWidth() * scale ),
								  (int) ( getDrawable().getIntrinsicHeight() * scale ) );
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
	 * @param imageWidth
	 * @param viewHeight
	 * @param imageHeight
	 * @return
	 */
	protected float getInitialScale( int viewWidth, int imageWidth, int viewHeight, int imageHeight )
	{
		return Math.min( viewWidth / (float) imageWidth, viewHeight / (float) imageHeight );
	}
}