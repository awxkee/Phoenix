package com.github.dozzatq.phoenix.share;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;

import com.github.dozzatq.R;

public class PhoenixShareActivity extends AppCompatActivity {

    private RecyclerView shareView;
    private DefaultItemAnimator itemAnimator;

    private Toolbar toolbar;
    private PhoenixShareAdapter blazeShareAdapter;
    private String broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide explode = new Slide();
            explode.setDuration(250);
            explode.setSlideEdge(Gravity.BOTTOM);
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(explode);
            getWindow().setExitTransition(explode);
            getWindow().setAllowEnterTransitionOverlap(true);
            getWindow().setAllowReturnTransitionOverlap(true);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoenix_share);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        if (getIntent().getStringExtra(PhoenixShare.SEND_SHARE_TITLE)==null)
            setTitle(getIntent().getStringExtra(Intent.EXTRA_SUBJECT));
        else setTitle(getIntent().getStringExtra(PhoenixShare.SEND_SHARE_TITLE));
        shareView = (RecyclerView) findViewById(R.id.recyclerShareView);
        itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        shareView.setItemAnimator(itemAnimator);
        shareView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));

        Bundle shareBundle = getIntent().getExtras();
        Intent shareIntent = new Intent(getIntent().getStringExtra(PhoenixShare.SEND_SHARE_ACTION));
        shareIntent.setType(getIntent().getStringExtra(PhoenixShare.SEND_SHARE_TYPE));
        shareIntent.putExtras(shareBundle);
        broadcastReceiver = getIntent().getStringExtra(PhoenixShare.SEND_SHARE_PARAM_RECEIVER);

        blazeShareAdapter = new PhoenixShareAdapter(this, shareIntent,broadcastReceiver );

        shareView.setAdapter(blazeShareAdapter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                }
                else onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
