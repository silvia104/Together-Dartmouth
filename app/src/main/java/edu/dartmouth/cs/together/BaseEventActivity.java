package edu.dartmouth.cs.together;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;


import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.dartmouth.cs.together.cloud.PostEventIntentService;
import edu.dartmouth.cs.together.cloud.QaIntentService;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.Qa;
import edu.dartmouth.cs.together.data.QaDataSource;
import edu.dartmouth.cs.together.utils.Globals;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class BaseEventActivity extends BasePopoutActivity implements
        LoaderManager.LoaderCallbacks<List<Qa>> {
    @Bind(R.id.bottomRecView) RecyclerView mBottomRecView;
    @Bind(R.id.locationText) TextView mLocationTv;
    @Bind(R.id.categoryText) TextView mCategoryTv;
    @Bind(R.id.coordinatorLayout) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.categoryRecView) RecyclerView mCategoryRecView;
    @Bind(R.id.fab) FloatingActionButton mFab;
    @Bind(R.id.dateText) TextView mDateText;
    @Bind(R.id.timeText) TextView mTimeText;
    @Bind(R.id.durationCount) TextView mDuration;
    @Bind(R.id.addDuration) ImageButton mAddDuration;
    @Bind(R.id.decreateDuration) ImageButton mDecreaseDuration;
    @Bind(R.id.shortDescText) TextView mShortDesc;
    @Bind(R.id.longDesc) TextView mLongDesc;
    @Bind(R.id.cancelBtn) Button mCancelButton;
    @Bind(R.id.postBtn) Button mPostButton;
    @Bind(R.id.pinInMap) ImageButton mPinInMap;
    @Bind(R.id.seekBar) SeekBar mLimit;
    @Bind(R.id.limitCount) TextView mLimitCount;
    @Bind(R.id.loading_progress) ProgressBar mProgress;
    @Bind(R.id.event_content_layout) RelativeLayout mContentlayout;
    @Bind(R.id.addQuestion) ImageButton mAddQuestion;
    @Bind(R.id.joinBtn) ImageButton mJoinBtn;
    protected BottomSheetBehavior mBtmShtBehavior;
    protected int mCategoryIdx = -1;
    protected LatLng mLatLng;
    protected int mLimitNum;
    protected Event mEvent;
    protected String action = Globals.ACTION_NOTHING;
    protected Menu mMenu;
    protected static final int SHORT_DESCRIPTION_DIALOG = 0;
    protected static final int LONG_DESCRIPTION_DIALOG = 1;
    protected static final int LOCATION_DIALOG = 2;
    protected static final int QUESTION_DIALOG = 3;
    protected static final int ANSWER_DIALOG = 4;

    protected QaRecVewAdapter mQaAdapter;

    protected BroadcastReceiver mDateReloadReceiver, mEventUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);
        ButterKnife.bind(this);
        setBottomSheet();
        mCategoryRecView.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        mQaAdapter = new QaRecVewAdapter(this, new ArrayList<Qa>(), false);
        setBottonRecView(mQaAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Intent i = new Intent(getApplicationContext(),QaIntentService.class);
                i.putExtra(Event.ID_KEY, mEvent.getEventId());
                startService(i);
                Intent i1 = new Intent(getApplicationContext(),PostEventIntentService.class);
                i1.putExtra(Event.ID_KEY, mEvent.getEventId());
                i1.putExtra(Globals.ACTION_KEY, Globals.ACTION_POLL);
                startService(i1);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setBottonRecView(RecyclerView.Adapter adapter) {
        mBottomRecView.setHasFixedSize(false);
        mBottomRecView.setLayoutManager(new LinearLayoutManager(this));
        mBottomRecView.setAdapter(adapter);
    }

    protected void setBottomSheet() {
        View bottomSheet = mCoordinatorLayout.findViewById(R.id.bottom_sheet);
        mBtmShtBehavior = BottomSheetBehavior.from(bottomSheet);
        setBtmShtBehavior();
    }

    protected void setBtmShtBehavior() {
        mBtmShtBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_SETTLING:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mFab.show();
                        break;
                    default:
                        mFab.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });
        mBtmShtBehavior.setPeekHeight(80);
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        Intent i = new Intent(this, JoinerListActivity.class);
        i.putExtra(Event.ID_KEY, mEvent.getEventId());
        startActivity(i);
    }

    protected void displayEventValues(Event event) {
        if (event == null) return;
        mCategoryTv.setText(event.getCategotyName());
        mCategoryTv.setTag("");
        mCategoryIdx = event.getCategoryIdx();
        mShortDesc.setText(event.getShortdesc());
        mShortDesc.setTag("");
        mLongDesc.setText(event.getLongDesc());
        mLocationTv.setText(event.getLocation());
        mLocationTv.setTag("");
        mDuration.setText("" + event.getDuration());
        mDateText.setText(event.getDate());
        mDateText.setTag("");
        mTimeText.setText(event.getTime());
        mTimeText.setTag("");
        mLimitNum = event.getLimit();
        mLimitCount.setText("Jointer Number Limit: " + mLimitNum);
        mLimit.setProgress(mLimitNum);
        mLatLng = event.getLatLng();
    }

    protected void disableLimitSeekbar() {
        Drawable thumb = mLimit.getThumb();
        thumb.setTint(0x664E342E);
        thumb.mutate();
        Drawable progress = mLimit.getProgressDrawable();
        progress.setTint(0x664E342E);
        progress.mutate();
        mLimit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    protected Dialog createInputDialog(int type) {
        View v = this.getLayoutInflater().inflate(R.layout.dialog_input, null);
        EditText shortDesc = (EditText) v.findViewById(R.id.editShortDesc);
        EditText longDesc = (EditText) v.findViewById(R.id.editLongDesc);
        ;
        String title = "";
        switch (type) {
            case SHORT_DESCRIPTION_DIALOG:
                longDesc.setVisibility(View.GONE);
                title = "Input Short Description";
                if (mShortDesc.getText().length() > 0) {
                    shortDesc.setText(mShortDesc.getText());
                    shortDesc.setSelection(shortDesc.getText().length());
                }
                break;
            case LONG_DESCRIPTION_DIALOG:
                shortDesc.setVisibility(View.GONE);
                title = "Input Long Description";
                if (mLongDesc.getText().length() > 0) {
                    longDesc.setText(mLongDesc.getText());
                    longDesc.setSelection(longDesc.getText().length());
                }
                break;
            case LOCATION_DIALOG:
                shortDesc.setVisibility(View.GONE);
                title = "Input Location Description";
                break;
            case QUESTION_DIALOG:
                shortDesc.setVisibility(View.GONE);
                title = "Input Question";
                break;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v)
                .setTitle(title);
        // Create the AlertDialog object and return it
        return builder.create();
    }

    protected void ShowInputDialog(final int type, final String... args) {
        final Dialog inputDialog = createInputDialog(type);
        inputDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button cancel = (Button) inputDialog.findViewById(R.id.cancelBtn);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputDialog.cancel();
                    }
                });
                Button save = (Button) inputDialog.findViewById(R.id.saveBtn);
                save.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText editText;
                                switch (type) {
                                    case LONG_DESCRIPTION_DIALOG:
                                        editText = (EditText) inputDialog.findViewById(R.id.editLongDesc);
                                        mLongDesc.setText(editText.getText());
                                        inputDialog.cancel();
                                        break;
                                    case SHORT_DESCRIPTION_DIALOG:
                                        editText = (EditText) inputDialog.findViewById(R.id.editShortDesc);
                                        if (editText.getText().length() == 0) {
                                            editText.setError("Short description can't be empty!");
                                        } else {
                                            mShortDesc.setText(editText.getText());
                                            mShortDesc.setTag("");
                                            inputDialog.cancel();

                                        }
                                        break;
                                    case LOCATION_DIALOG:
                                        editText = (EditText) inputDialog.findViewById(R.id.editLongDesc);
                                        if (editText.getText().length() == 0) {
                                            editText.setError("Location description can't be empty!");
                                        } else {
                                            mLocationTv.setText(editText.getText());
                                            mLocationTv.setTag("");
                                            inputDialog.cancel();
                                        }
                                        break;
                                    case QUESTION_DIALOG:
                                        showQaDialog(inputDialog,QUESTION_DIALOG, -1);
                                        break;
                                    case ANSWER_DIALOG:
                                        showQaDialog(inputDialog,ANSWER_DIALOG,
                                                Long.parseLong(args[0]));
                                        break;
                                }
                            }

                        }

                );
            }
        });
        inputDialog.show();
        inputDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        inputDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void showQaDialog(Dialog inputDialog, int type, long  qaId){
        String errorPrx ="", key ="";
        EditText editText = (EditText) inputDialog.findViewById(R.id.editLongDesc);
        EditText editText1 = (EditText) inputDialog.findViewById(R.id.editShortDesc);
        editText1.setVisibility(View.GONE);
        if (editText.getText().length() == 0) {
            if (type == QUESTION_DIALOG){
                errorPrx = "Question";
            } else {
                errorPrx = "Answer";
            }
            editText.setError(errorPrx +" can't be empty!");
        } else {
            Qa qa = null;
            Intent i = new Intent(getApplicationContext(),
                    QaIntentService.class);
            String text = editText.getText().toString();
            if (type == QUESTION_DIALOG){
                key = Qa.Q_KEY;
                qa = new Qa(text);
                qa.setEventId(mEvent.getEventId());
                i.putExtra(Qa.ID_KEY, qa.getId());
            } else {
                key = Qa.A_KEY;
                i.putExtra(Qa.ID_KEY,qaId);

            }
            i.putExtra(key,text);
            i.putExtra(Event.ID_KEY,mEvent.getEventId());
            startService(i);
            inputDialog.cancel();
        }
    }

    @Override
    public Loader<List<Qa>> onCreateLoader(int id, Bundle args) {
       return null;
    }

    @Override
    public void onLoadFinished(Loader<List<Qa>> loader, List<Qa> data) {

    }

    @Override
    public void onLoaderReset(Loader<List<Qa>> loader) {

    }

    static  class QaLoader extends AsyncTaskLoader<List<Qa>> {
        private Context mContext;
        private long mEventId;
        public QaLoader(Context context, long eventId) {
            super(context);
            mContext = context;
            mEventId = eventId;
        }

        @Override
        public List<Qa> loadInBackground() {
            return new QaDataSource(mContext).queryQaByEventId(mEventId);
        }
    }

    protected class LoadEventAsyncTask extends AsyncTask<Long, Void, Event> {
        private int mEventType;

        public LoadEventAsyncTask(int eventType) {
            mEventType = eventType;
        }

        @Override
        protected Event doInBackground(Long... ids) {
            EventDataSource db = new EventDataSource(getApplicationContext());
            Event event = null;
            long id = ids[0];
            if (id != -1) {
                event = db.queryEventById(mEventType, id);
            }
            return event;
        }

        @Override
        protected void onPostExecute(Event event) {
            super.onPostExecute(event);
            if (event == null) {
                Toast.makeText(getApplicationContext(), "Event is deleted!", Toast.LENGTH_SHORT);
                finish();
            } else {
                mProgress.setVisibility(View.GONE);
                mContentlayout.setVisibility(View.VISIBLE);
                mEvent = event;
                action = edu.dartmouth.cs.together.utils.Globals.ACTION_UPDATE;
                displayEventValues(event);
                //TODO:待删
                mLimitCount.setText(mEvent.getmJoinerCount()+"/" + mEvent.getLimit());

                if(mEvent.getmJoinerCount()>=mEvent.getLimit()){
                    mJoinBtn.setEnabled(false);
                }
            }
        }
    }

    class QaRecVewAdapter extends RecyclerView.Adapter<QaRecVewAdapter.QaViewHolder>{
        private Context mContext;
        private LayoutInflater mInflater;
        private List<Qa> mList= new ArrayList<>() ;
        private boolean mIsEdit;
        public QaRecVewAdapter(Context context, List<Qa> qaList, boolean isEdit) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            if (qaList.size() == 0){
                mList.add(new Qa("No Question Yet!"));
            } else {
                mList.addAll(qaList);
            }
            mIsEdit = isEdit;
        }

        @Override
        public QaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mInflater.inflate(R.layout.item_qa,parent,false);
            return new QaViewHolder(v);
        }

        @Override
        public void onBindViewHolder(QaViewHolder holder, int i) {
            holder.mQuestion.setText(mList.get(i).getQuestion());
            holder.mAnswer.setText(mList.get(i).getAnswer());
            holder.mAnswer.setVisibility(View.GONE);
            holder.mEditAnswer.setVisibility(View.GONE);
            holder.mQuestion.setTag(mList.get(i).getId());
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void updateData(List<Qa> list){
            mList.clear();
            if (list.size() == 0){
                mList.add(new Qa("No Question Yet!"));
            } else {
                mList.addAll(list);
            }

            notifyDataSetChanged();
        }
        public class QaViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.questionText)  TextView mQuestion;
            @Bind(R.id.expand_arrow)  ImageButton mControl;
            @Bind(R.id.answerText) TextView mAnswer;
            @Bind(R.id.editAnswer) ImageButton mEditAnswer;
            public QaViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mControl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onControlClick();
                    }
                });
            }
            @OnClick(R.id.questionText)
            public void onControlClick(){
                if (mControl.getTag().toString().equals("closed")){
                    mControl.setTag("open");
                    mControl.setImageResource(R.drawable.ic_expand_less);
                    mAnswer.setVisibility(View.VISIBLE);
                    if (mIsEdit){
                        mEditAnswer.setVisibility(View.VISIBLE);
                    } else {
                        mEditAnswer.setVisibility(View.GONE);
                    }
                } else {
                    mControl.setTag("closed");
                    mControl.setImageResource(R.drawable.ic_expand_more);
                    mAnswer.setVisibility(View.GONE);
                    mEditAnswer.setVisibility(View.GONE);
                }
            }

            @OnClick(R.id.editAnswer)
            public void onEidtClick(){
                ShowInputDialog(ANSWER_DIALOG, mQuestion.getTag().toString());
            }

        }
    }

}
