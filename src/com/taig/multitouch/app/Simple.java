package com.taig.multitouch.app;

import android.app.Activity;
import android.os.Bundle;

import com.taig.multitouch.R;
import com.taig.multitouch.widget.MultitouchImageView;

public class Simple extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		MultitouchImageView imageView = new MultitouchImageView( this );
		imageView.setImageDrawable( getResources().getDrawable( R.drawable.rick ) );

		setContentView( imageView );
	}
}