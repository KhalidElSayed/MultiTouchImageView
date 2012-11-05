package com.taig.app;

import android.app.Activity;
import android.os.Bundle;

import com.taig.R;
import com.taig.widget.StretchedImageView;

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