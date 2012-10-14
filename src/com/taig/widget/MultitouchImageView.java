package com.taig.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

/**
 * An extension of android's native {@link ImageView} supporting pinch-zoom,
 * double-tap-zoom and exploring the image via drag-gestures.
 */
public class MultitouchImageView extends ImageView
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
	 * The position of the user's last touch.
	 */
	private PointF			lastTouch			= new PointF( 0, 0 );

	/**
	 * The recently applied scaling.
	 */
	private float			lastScale			= 1;

	public MultitouchImageView( Context context )
	{
		this( context, null );
	}

	public MultitouchImageView( Context context, AttributeSet attrs )
	{
		super( context, attrs );

		this.setImageMatrix( new Matrix() );
		this.setScaleType( ScaleType.MATRIX );
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
	
	public PointF getLastTouch()
	{
		return lastTouch;
	}

	public void setLastTouch( float x, float y )
	{
		this.lastTouch.x = x;
		this.lastTouch.y = y;
	}

	public void setLastTouch( MotionEvent event )
	{
		this.setLastTouch( event.getX(), event.getY() );
	}

	public float getLastScale()
	{
		return lastScale;
	}

	public void setLastScale( float lastScale )
	{
		this.lastScale = lastScale;
	}

	/**
	 * Reset the drawable's position and size to its original condition.
	 * 
	 * @return <code>true</code> if the current image matrix has been adjusted
	 *         to the initial matrix. <code>false</code> if the current image
	 *         matrix has already been reset.
	 */
	public boolean reset()
	{
		if( matrixValues.equals( initialStateValues ) )
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

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
	{
		int width = MeasureSpec.getSize( widthMeasureSpec );
		int height = MeasureSpec.getSize( heightMeasureSpec );
		setMeasuredDimension( width, height );

		if( getDrawable() != null && getImageMatrix().isIdentity() )
		{
			getImageMatrix().getValues( matrixValues );

			// Scale image to match parent.
			scale( getScale( width, height ), 0, 0 );

			// Store this position as initial position.
			getImageMatrix().getValues( this.initialStateValues );
		}
	}

	protected float getScale( int viewWidth, int viewHeight )
	{
		return Math.min( viewWidth / (float) getDrawable().getIntrinsicWidth(), viewHeight / (float) getDrawable().getIntrinsicHeight() );
	}

	protected float getOffset( Axis axis, int viewSize, int drawableSize, float scale )
	{
		if( axis.equals( Axis.X ) )
		{
			return ( viewSize - drawableSize * scale ) / 2;
		}
		else
		{
			return drawableSize * scale < viewSize ? ( viewSize - drawableSize * scale ) / 2 : 0;
		}
	}

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

		setLastTouch( getLastTouch().x + deltaX, getLastTouch().y + deltaY );
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
						setLastTouch( event );
					break;

					case MotionEvent.ACTION_MOVE:
						// Translate each movement event into the image matrix
						// and update the last touched position afterwards.
						translate( event.getX() - lastTouch.x, event.getY() - lastTouch.y );
					break;
				}
			}

			// Enforce the view to be redrawn.
			invalidate();

			return true;
		}
	}
}