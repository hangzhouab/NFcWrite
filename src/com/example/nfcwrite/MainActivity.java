package com.example.nfcwrite;

import java.io.IOException;
import java.nio.charset.Charset;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.*;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	Button bt;
	Intent in;
	PendingIntent mPendingIntent;
	IntentFilter[] mFilters;
	String[][] mTechLists;
	NfcAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bt = (Button) findViewById(R.id.button1);
		in = getIntent();
		bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				writeNdefTag(in);

			}
		});
		mAdapter = NfcAdapter.getDefaultAdapter(this);

		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// 做一个IntentFilter过滤你想要的action 这里过滤的是ndef
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		// 如果你对action的定义有更高的要求，比如data的要求，你可以使用如下的代码来定义intentFilter
		// try {
		// ndef.addDataType("*/*");
		// } catch (MalformedMimeTypeException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// 生成intentFilter
		mFilters = new IntentFilter[] { ndef, };

		// 做一个tech-list。可以看到是二维数据，每一个一维数组之间的关系是或，但是一个一维数组之内的各个项就是与的关系了
		mTechLists = new String[][] { new String[] { NfcF.class.getName() },
				new String[] { NfcA.class.getName() },
				new String[] { NfcB.class.getName() },
				new String[] { NfcV.class.getName() } };

	}

	private void writeNdefTag(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tag == null) {
			Log.i("null", "null");
		}
		Log.i("TAGss", tag.toString());
		Ndef ndef = Ndef.get(tag);
		try {
			ndef.connect();
			NdefMessage msg = new NdefMessage(
					new NdefRecord[] { createRecord() });
			ndef.writeNdefMessage(msg);
			Log.i("msg", msg.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FormatException e) {

		}

	}

	public void onResume() {
		super.onResume();
		// 设定intentfilter和tech-list。如果两个都为null就代表优先接收任何形式的TAG
		// action。也就是说系统会主动发TAG intent。
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
				mTechLists);
	}

	private NdefRecord createRecord() {
		return new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI,
				"http://www.baidu.com".getBytes(Charset.forName("US-ASCII")), new byte[0],
				new byte[0]);
	}
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
