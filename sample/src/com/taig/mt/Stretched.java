package com.taig.mt;

import android.app.Activity;
import android.os.Bundle;

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