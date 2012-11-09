package com.taig.widget;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * An extension of android's native {@link ImageView} supporting pinch-zoom,
 * double-tap-zoom and exploring the image via drag-gestures.
 */
public class MultitouchImageView extends StretchedImageView
{
	public enum Axis
	{
		X, Y;
	}

	/**
	 * The maximum allowed scale factor.
	 */
	private float			maxScale			= 3.5f;

	/**
	 * The minimum allowed scale factor.
	 */
	private float			minScale			= 1f;

	/**
	 * Maximum scale factor for double taps.
	 */
	private float			doubleTapScale		= 2f;

	/**
	 * The {@link TouchListener} that manages drag, pinch and scale gestures.
	 */
	private TouchListener	touchListener;

	/**
	 * The image's initial (after {@link #onMeasure(int, int)} was performed)
	 * state array representation.
	 */
	private float[]			initialStateValues	= new float[9];

	/**
	 * The array representation of the currently active image {@link Matrix}.
	 */
	private float[]			matrixValues		= new float[9];

	/**
	 * An additional {@link Matrix} used for scaling.
	 */
	private Matrix			scaleMatrix			= new Matrix();

	/**
	 * The {@link #scaleMatrix} in array representation.
	 */
	private float[]			scaleMatrixValues	= new float[9];

	/**
	 * The position of the last pointer interaction.
	 */
	private PointF			lastPosition		= new PointF( 0, 0 );

	/**
	 * The recently applied scaling.
	 */
	private float			lastScale			= 1;

	public MultitouchImageView( Context context )
	{
		this( context, null, 0 );
	}

	public MultitouchImageView( Context context, AttributeSet attrs )
	{
		this( context, attrs, 0 );
	}

	public MultitouchImageView( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );

		this.touchListener = new TouchListener( context );
		this.setOnTouchListener( this.touchListener );
	}

	public float getMaxScale()
	{
		return maxScale;
	}

	public void setMaxScale( float maxScale )
	{
		this.maxScale = maxScale;
	}

	public float getMinScale()
	{
		return minScale;
	}

	public void setMinScale( float minScale )
	{
		this.minScale = minScale;
	}

	public float getDoubleTapScale()
	{
		return doubleTapScale;
	}

	public void setDoubleTapScale( float doubleTapScale )
	{
		this.doubleTapScale = doubleTapScale;
	}

	/**
	 * Get the image's current scale factor.
	 * 
	 * @return
	 */
	public float getCurrentScale()
	{
		return matrixValues[Matrix.MSCALE_X];
	}

	public PointF getLastPosition()
	{
		return lastPosition;
	}

	public void setLastPosition( float x, float y )
	{
		this.lastPosition.x = x;
		this.lastPosition.y = y;
	}

	public void setLastPosition( MotionEvent event )
	{
		this.setLastPosition( event.getX(), event.getY() );
	}

	public float getLastScale()
	{
		return lastScale;
	}

	public void setLastScale( float lastScale )
	{
		this.lastScale = lastScale;
	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
	{
		super.onMeasure( widthMeasureSpec, heightMeasureSpec );

		getImageMatrix().getValues( matrixValues );

		if( !Arrays.equals( matrixValues, initialStateValues ) )
		{
			// Set dimensions to fill the viewport.
			setMeasuredDimension( MeasureSpec.getSize( widthMeasureSpec ), MeasureSpec.getSize( heightMeasureSpec ) );

			// Use translate method to exploit its functionalities to center the
			// image.
			translate( 0, 0 );

			// Store this position as initial position.
			getImageMatrix().getValues( initialStateValues );
		}
	}

	/**
	 * Reset the drawable's position and size to its original condition.
	 * 
	 * @return <code>true</code> if the current image matrix' scale has been
	 *         adjusted to the initial matrix' scale. <code>false</code> if the
	 *         current image matrix has already been reset.
	 */
	public boolean reset()
	{
		getImageMatrix().getValues( matrixValues );

		if( matrixValues[Matrix.MSCALE_X] == initialStateValues[Matrix.MSCALE_X] || getImageMatrix().isIdentity() )
		{
			return false;
		}
		else
		{
			getImageMatrix().setValues( initialStateValues );
			invalidate();
			return true;
		}
	}

	/**
	 * Get the top or left offset to keep the image centered.
	 * 
	 * @param axis
	 * @param viewSize
	 * @param drawableSize
	 * @param scale
	 * @return
	 */
	protected float getOffset( Axis axis, int viewSize, int drawableSize, float scale )
	{
		if( axis == Axis.X )
		{
			return ( viewSize - drawableSize * scale ) / 2;
		}
		else
		{
			return drawableSize * scale < viewSize ? ( viewSize - drawableSize * scale ) / 2 : 0;
		}
	}

	/**
	 * Moves the image from its current position. This method does not accept
	 * absolute coordinates. The movement has to be specified via relative delta
	 * values.
	 * 
	 * @param deltaX
	 *            The amount of pixels to move the image on the X-axis.
	 * @param deltaY
	 *            The amount of pixels to move the image on the Y-axis.
	 */
	public void translate( float deltaX, float deltaY )
	{
		matrixValues[Matrix.MTRANS_X] = getTranslation(
			Axis.X,
			getDrawable().getIntrinsicWidth(),
			getCurrentScale(),
			getMeasuredWidth(),
			matrixValues[Matrix.MTRANS_X],
			deltaX );

		matrixValues[Matrix.MTRANS_Y] = getTranslation(
			Axis.Y,
			getDrawable().getIntrinsicHeight(),
			getCurrentScale(),
			getMeasuredHeight(),
			matrixValues[Matrix.MTRANS_Y],
			deltaY );

		setLastPosition( getLastPosition().x + deltaX, getLastPosition().y + deltaY );
		getImageMatrix().setValues( matrixValues );
	}

	protected float getTranslation( Axis axis, int drawableSize, float scale, int viewSize, float translation, float delta )
	{
		float currentDrawableSize = drawableSize * scale;

		if( currentDrawableSize <= viewSize )
		{
			return getOffset( axis, viewSize, drawableSize, scale );
		}
		else
		{
			return delta > 0
				? Math.min( 0, translation + delta )
				: Math.max( -1 * ( currentDrawableSize - viewSize ), translation + delta );
		}
	}

	public void scale( float scale, float x, float y )
	{
		if( scale > 1 )
		{
			scale = Math.min( scale, maxScale / getCurrentScale() );
		}
		else if( scale < 1 )
		{
			scale = Math.max( scale, initialStateValues[Matrix.MSCALE_X] / getCurrentScale() );
		}

		// Perform scaling on a separate matrix to prevent its translations from
		// leaving the view's bounds.
		scaleMatrix.set( getImageMatrix() );
		scaleMatrix.postScale( scale, scale, x, y );
		scaleMatrix.getValues( scaleMatrixValues );

		setLastScale( scale );

		matrixValues[Matrix.MSCALE_X] = scaleMatrixValues[Matrix.MSCALE_X];
		matrixValues[Matrix.MSCALE_Y] = scaleMatrixValues[Matrix.MSCALE_Y];

		translate(
			scaleMatrixValues[Matrix.MTRANS_X] - matrixValues[Matrix.MTRANS_X],
			scaleMatrixValues[Matrix.MTRANS_Y] - matrixValues[Matrix.MTRANS_Y] );
	}

	protected class TouchListener implements OnTouchListener
	{
		private GestureDetector			doubleTapDetector;

		private ScaleGestureDetector	scaleDetector;

		public TouchListener( Context context )
		{
			this.doubleTapDetector = new GestureDetector( context, new GestureDetector.SimpleOnGestureListener()
			{
				@Override
				public boolean onSingleTapConfirmed( MotionEvent event )
				{
					View view = MultitouchImageView.this;

					if( !performClick() )
					{
						while( view.getParent() instanceof View )
						{
							view = (View) view.getParent();

							if( view.performClick() )
							{
								return true;
							}
						}

						return false;
					}

					return true;
				}

				@Override
				public void onLongPress( MotionEvent event )
				{
					View view = MultitouchImageView.this;

					if( !performLongClick() )
					{
						while( view.getParent() instanceof View )
						{
							view = (View) view.getParent();

							if( view.performLongClick() )
							{
								return;
							}
						}
					}
				}

				@Override
				public boolean onDoubleTap( MotionEvent event )
				{
					if( getCurrentScale() > initialStateValues[Matrix.MSCALE_X] )
					{
						// Zoom out.
						scale( initialStateValues[Matrix.MSCALE_X] / getCurrentScale(), event.getX(), event.getY() );
					}
					else
					{
						// Zoom in.
						scale( doubleTapScale / getCurrentScale(), event.getX(), event.getY() );
					}

					return true;
				}
			} );

			this.scaleDetector = new ScaleGestureDetector( context, new ScaleGestureDetector.SimpleOnScaleGestureListener()
			{
				@Override
				public boolean onScale( ScaleGestureDetector detector )
				{
					scale( detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY() );
					return true;
				}
			} );
		}

		public GestureDetector getDoubleTapDetector()
		{
			return doubleTapDetector;
		}

		public void setDoubleTapDetector( GestureDetector doubleTapDetector )
		{
			this.doubleTapDetector = doubleTapDetector;
		}

		public ScaleGestureDetector getScaleDetector()
		{
			return scaleDetector;
		}

		public void setScaleDetector( ScaleGestureDetector scaleDetector )
		{
			this.scaleDetector = scaleDetector;
		}

		@Override
		public boolean onTouch( View view, MotionEvent event )
		{
			// Pass the touch event to the detectors to process further events.
			getImageMatrix().getValues( matrixValues );
			doubleTapDetector.onTouchEvent( event );
			scaleDetector.onTouchEvent( event );

			// Manage the actual drag-event.
			if( !scaleDetector.isInProgress() )
			{
				switch( event.getAction() )
				{
					case MotionEvent.ACTION_DOWN:
						// Store touch position as point on first interaction.
						setLastPosition( event );
					break;

					case MotionEvent.ACTION_MOVE:
						// Translate each movement event into the image matrix
						// and update the last touched position afterwards.
						translate( event.getX() - lastPosition.x, event.getY() - lastPosition.y );
					break;
				}
			}

			// Enforce the view to be redrawn.
			invalidate();

			return true;
		}
	}
}