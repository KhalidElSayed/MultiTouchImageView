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

public class MultitouchImageView extends ImageView
{
	public enum Axis
	{
		X, Y;
	}

	private float	maxScale			= 3.5f;

	private float	minScale			= 1f;

	private Matrix	initialState		= new Matrix();

	private float[]	initialStateValues	= new float[9];

	private float[]	matrixValues		= new float[9];

	private Matrix	scaleMatrix			= new Matrix();

	private float[]	scaleMatrixValues	= new float[9];

	public MultitouchImageView( Context context )
	{
		this( context, null );
	}

	public MultitouchImageView( Context context, AttributeSet attrs )
	{
		super( context, attrs );

		this.setImageMatrix( new Matrix() );
		this.setScaleType( ScaleType.MATRIX );
		this.setOnTouchListener( new TouchListener( context ) );
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

	/**
	 * Reset the drawable's position and size to its original condition.
	 * 
	 * @return
	 */
	public boolean reset()
	{
		if( getImageMatrix().equals( initialState ) )
		{
			return false;
		}
		else
		{
			getImageMatrix().set( initialState );
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

		if( getDrawable() != null && initialState.isIdentity() )
		{
			getImageMatrix().getValues( matrixValues );

			// Scale image to match parent.
			scale( getScale( width, height ), 0, 0 );

			// Store this position as initial position.
			this.initialState.set( getImageMatrix() );
			this.initialState.getValues( this.initialStateValues );
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
			matrixValues[Matrix.MSCALE_X],
			getMeasuredWidth(),
			matrixValues[Matrix.MTRANS_X],
			deltaX );

		matrixValues[Matrix.MTRANS_Y] = getTranslation(
			Axis.Y,
			getDrawable().getIntrinsicHeight(),
			matrixValues[Matrix.MSCALE_Y],
			getMeasuredHeight(),
			matrixValues[Matrix.MTRANS_Y],
			deltaY );

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
			scale = Math.min( scale, maxScale / matrixValues[Matrix.MSCALE_X] );
		}
		else if( scale < 1 )
		{
			scale = Math.max( scale, initialStateValues[Matrix.MSCALE_X] / matrixValues[Matrix.MSCALE_X] );
		}

		// Perform scaling on a separate matrix to prevent its translations from
		// leaving the view's bounds.
		scaleMatrix.set( getImageMatrix() );
		scaleMatrix.postScale( scale, scale, x, y );
		scaleMatrix.getValues( scaleMatrixValues );

		matrixValues[Matrix.MSCALE_X] = scaleMatrixValues[Matrix.MSCALE_X];
		matrixValues[Matrix.MSCALE_Y] = scaleMatrixValues[Matrix.MSCALE_Y];

		translate(
			scaleMatrixValues[Matrix.MTRANS_X] - matrixValues[Matrix.MTRANS_X],
			scaleMatrixValues[Matrix.MTRANS_Y] - matrixValues[Matrix.MTRANS_Y] );
	}

	protected class TouchListener implements OnTouchListener
	{
		private PointF					lastTouch	= new PointF();

		private GestureDetector			doubleTapDetector;

		private ScaleGestureDetector	scaleDetector;

		public TouchListener( Context context )
		{
			this.doubleTapDetector = new GestureDetector( context, new GestureDetector.SimpleOnGestureListener()
			{
				@Override
				public boolean onDoubleTap( MotionEvent event )
				{
					scale( 2f, event.getX(), event.getY() );
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

		public PointF getLastTouch()
		{
			return lastTouch;
		}

		public void setLastTouch( PointF lastTouch )
		{
			this.lastTouch = lastTouch;
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
					setLastTouch( event );
				break;
			}

			// Enforce the view to be redrawn.
			invalidate();

			return true;
		}

		private void setLastTouch( MotionEvent event )
		{
			this.lastTouch.set( event.getX(), event.getY() );
		}
	}
}