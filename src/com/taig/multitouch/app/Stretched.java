package com.taig.multitouch.app;

import android.app.Activity;
import android.os.Bundle;

import com.taig.multitouch.R;
import com.taig.multitouch.widget.StretchedImageView;

public class Stretched extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		StretchedImageView imageView = new StretchedImageView( this );
		imageView.setImageDrawable( getResources().getDrawable( R.drawable.rick ) );

		setContentView( imageView );
	}
}