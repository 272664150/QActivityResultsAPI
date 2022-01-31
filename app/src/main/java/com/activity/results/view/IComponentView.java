package com.activity.results.view;

import android.content.Context;
import android.util.AttributeSet;

public interface IComponentView {

    void init(Context context);

    void parseXmlAttributes(Context context, AttributeSet attributeSet);

    void bindData();
}
