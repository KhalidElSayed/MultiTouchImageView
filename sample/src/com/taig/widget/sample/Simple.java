package com.taig.widget.sample;

import android.app.Activity;
import android.os.Bundle;
import com.taig.widget.MultiTouchImageView;

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