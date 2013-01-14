package com.taig.mt;

import android.app.Activity;
import android.os.Bundle;

public class Simple extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		MultiTouchImageView imageView = new MultiTouchImageView( this );
		imageView.setImageDrawable( getResources().getDrawable( R.drawable.rick ) );

		setContentView( imageView );
	}
}