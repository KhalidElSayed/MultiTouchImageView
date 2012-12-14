package com.taig.multitouch.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.taig.multitouch.R;

public class Main extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );

		Button stretched = (Button) findViewById( R.id.button_stretched );
		stretched.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				startActivity( new Intent( Main.this, Stretched.class ) );
			}
		} );

		Button simple = (Button) findViewById( R.id.button_multitouch_simple );
		simple.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				startActivity( new Intent( Main.this, Simple.class ) );
			}
		} );

		Button advanced = (Button) findViewById( R.id.button_multitouch_advanced );
		advanced.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				startActivity( new Intent( Main.this, Advanced.class ) );
			}
		} );
	}
}