package com.kogasoftware.odt.invehicledevice.modal;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kogasoftware.odt.invehicledevice.R;

public class Modal extends LinearLayout implements OnTouchListener {
	@Deprecated
	public static AttributeSet getDefaultAttributeSet(Resources resources) {
		XmlPullParser parser = resources.getXml(R.xml.modal_attribute_set);
		while (true) {
			int state = 0;
			try {
				state = parser.next();
			} catch (XmlPullParserException e) {
				return null;
			} catch (IOException e) {
				return null;
			}
			if (state == XmlPullParser.END_DOCUMENT) {
				return null;
			}
			if (state == XmlPullParser.START_TAG
					&& parser.getName().equals("ModalAttributeSet")) {
				return Xml.asAttributeSet(parser);
			}
		}
	}

	private void init(Context context, int resourceId) {
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// setOnTouchListener(this);
		this.addView(layoutInflater.inflate(resourceId, null),
				new Modal.LayoutParams(Modal.LayoutParams.FILL_PARENT,
						Modal.LayoutParams.FILL_PARENT));
	}

	public Modal(Context context, int resourceId) {
		super(context);
		init(context, resourceId);
	}

	public Modal(Context context, AttributeSet attrs, int resourceId) {
		super(context, attrs);
		init(context, resourceId);
	}

	public void close() {
		FrameLayout modals = (FrameLayout) getRootView().findViewById(
				R.id.modal_layout);
		modals.removeView(this);
	}

	public void open(View rootView) {
		FrameLayout modals = (FrameLayout) rootView
				.findViewById(R.id.modal_layout);
		modals.addView(this);
	}

	@Deprecated
	public void hide() {
		setVisibility(View.GONE);
	}

	private Float lastMotionEventX = 0f;

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		Toast.makeText(getContext(), "Hello", Toast.LENGTH_LONG).show();

		switch (motionEvent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastMotionEventX = motionEvent.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			layout((int) (getLeft() - lastMotionEventX + motionEvent.getX()),
					getTop(), getWidth(), getBottom());
			lastMotionEventX = motionEvent.getX();
			break;
		case MotionEvent.ACTION_UP:
			// layout(0, getTop(), getWidth(), getBottom());
			break;
		case MotionEvent.ACTION_CANCEL:
			// layout(0, getTop(), getWidth(), getBottom());
			break;
		}
		return true;
	}
}
