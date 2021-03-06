// Generated by view binder compiler. Do not edit!
package com.example.findpath5.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.findpath5.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final ImageView imageView;

  @NonNull
  public final Button indoor;

  @NonNull
  public final RelativeLayout rl;

  @NonNull
  public final Button startMapBtn;

  private ActivityMainBinding(@NonNull ConstraintLayout rootView, @NonNull ImageView imageView,
      @NonNull Button indoor, @NonNull RelativeLayout rl, @NonNull Button startMapBtn) {
    this.rootView = rootView;
    this.imageView = imageView;
    this.indoor = indoor;
    this.rl = rl;
    this.startMapBtn = startMapBtn;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.imageView;
      ImageView imageView = ViewBindings.findChildViewById(rootView, id);
      if (imageView == null) {
        break missingId;
      }

      id = R.id.indoor;
      Button indoor = ViewBindings.findChildViewById(rootView, id);
      if (indoor == null) {
        break missingId;
      }

      id = R.id.rl;
      RelativeLayout rl = ViewBindings.findChildViewById(rootView, id);
      if (rl == null) {
        break missingId;
      }

      id = R.id.start_map_btn;
      Button startMapBtn = ViewBindings.findChildViewById(rootView, id);
      if (startMapBtn == null) {
        break missingId;
      }

      return new ActivityMainBinding((ConstraintLayout) rootView, imageView, indoor, rl,
          startMapBtn);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
