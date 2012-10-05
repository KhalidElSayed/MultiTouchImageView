package com.taig.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.taig.R;

public class Main extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		
		Button simple = (Button) findViewById( R.id.simple );
		simple.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick( View view )
			{
				startActivity( new Intent( Main.this, Simple.class ) );
			}
		} );
		
		Button advanced = (Button) findViewById( R.id.advanced );
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