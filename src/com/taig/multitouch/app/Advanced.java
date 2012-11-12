package com.taig.multitouch.app;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

import com.taig.multitouch.R;
import com.taig.multitouch.widget.MultitouchImageView;

public class Advanced extends Activity
{
	private MultitouchImageView	imageView;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		imageView = new MultitouchImageView( this )
		{
			@Override
			protected float getInitialScale( int width, int height )
			{
				// Stretch image to always fill the view's width.
				return width / (float) getDrawable().getIntrinsicWidth();
			}

			@Override
			protected float getTranslation( Axis axis, int drawableSize, float scale, int viewSize, float translation, float delta )
			{
				// Ignore the 50px on the image's bottom in the calculations (to
				// hide the water mark) and ...
				if( axis.equals( Axis.Y ) )
				{
					drawableSize -= 60;
				}

				return super.getTranslation( axis, drawableSize, scale, viewSize, translation, delta );
			}

			@Override
			protected void onDraw( Canvas canvas )
			{
				// ... don't event draw those pixels.
				canvas.concat( getImageMatrix() );
				canvas.clipRect( 0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight() - 60 );
				getDrawable().draw( canvas );
			}
		};

		imageView.setLayoutParams( new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
		imageView.setImageDrawable( getResources().getDrawable( R.drawable.porn ) );
		imageView.setBackgroundColor( getResources().getColor( android.R.color.black ) );
		imageView.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				Toast.makeText( Advanced.this, "Click event.", Toast.LENGTH_SHORT ).show();
			}
		} );
		imageView.setOnLongClickListener( new OnLongClickListener()
		{
			@Override
			public boolean onLongClick( View view )
			{
				Toast.makeText( Advanced.this, "LongClick event.", Toast.LENGTH_SHORT ).show();
				return true;
			}
		} );

		setContentView( imageView );
	}

	@Override
	public void onBackPressed()
	{
		// Set image back to initial state before closing the activity.
		if( !imageView.reset() )
		{
			super.onBackPressed();
		}
	}
}