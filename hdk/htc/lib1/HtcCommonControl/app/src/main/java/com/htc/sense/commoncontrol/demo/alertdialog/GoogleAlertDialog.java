/*
 * Copyright (C) 2009 HTC Inc.
 */

package com.htc.sense.commoncontrol.demo.alertdialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.htc.lib1.cc.widget.HtcAlertDialog;
import com.htc.sense.commoncontrol.demo.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class GoogleAlertDialog extends Activity {

    private static final String TAG = "GoogleAlertDialog";

    private static final int DIALOG_ALL_MESSAGE_DIALOGS = 1; // message dialogs
    private static final int DIALOG_ALL_LIST_DIALOGS = 2; // list dialogs
    private static final int DIALOG_ALL_PROGRESS_DIALOGS = 3; // progress
    private static final int DIALOG_ACKNOWLEDGEMENT = 11; // two-line message, 1
    private static final int DIALOG_QUESTION = 12; // "longer, but less than one page"
    private static final int DIALOG_LEGAL_MESSAGE = 13; // legal message, super
    private static final int DIALOG_ONE_LINE = 14; // one line message, 3
    private static final int DIALOG_CHECK_ONLY = 15;// 3-lines title, 0 line text (no text)
    private static final int DIALOG_CHECK_MESSAGE = 16; // text message followed
    private static final int DIALOG_CHECK_MANY = 17;// 3-lines title, 0 line text (no text)
    private static final int DIALOG_LIST = 21; // list dialog (a few items, less
    private static final int DIALOG_SINGLE_CHOICE = 22; // single choice dialog
    private static final int DIALOG_MULTIPLE_CHOICES = 23; // multiple choices
    private static final int DIALOG_LONG_LIST = 24; // long list (more than one
    private static final int DIALOG_CHECK_LIST = 25; // check-box following list
    private static final int DIALOG_FONT_SELECTOR = 26; // pre-defined font
    private static final int DIALOG_SPINNER_PROGRESS = 31; // spinner progress
    private static final int DIALOG_SPINNER_LISTITEM = 33; // spinner progress
    private static final int DIALOG_HORIZONTAL_PROGRESS = 32; // horizontal
    private static final int DIALOG_HORIZONTAL_NO_MESSAGE = 34; // horizontal
    private static final int DIALOG_HORIZONTAL_LONG_MESSAGE = 35; // horizontal

    // lists for top 3 categories
    private static final int[] IDS_ALL_MESSAGE_DIALOGS = {
        DIALOG_ACKNOWLEDGEMENT, DIALOG_QUESTION, DIALOG_LEGAL_MESSAGE,
        DIALOG_ONE_LINE, DIALOG_CHECK_MESSAGE,DIALOG_CHECK_ONLY,DIALOG_CHECK_MANY
            /*
             * , DIALOG_INVERSEBKG
             */};

    private static final int[] IDS_ALL_LIST_DIALOGS = {
        DIALOG_LIST,
        DIALOG_SINGLE_CHOICE, DIALOG_MULTIPLE_CHOICES, DIALOG_LONG_LIST,
        DIALOG_CHECK_LIST, DIALOG_FONT_SELECTOR
    };

    private static final int[] IDS_ALL_PROGRESS_DIALOGS = {
        DIALOG_SPINNER_PROGRESS, DIALOG_SPINNER_LISTITEM,
        DIALOG_HORIZONTAL_PROGRESS, DIALOG_HORIZONTAL_NO_MESSAGE,
        DIALOG_HORIZONTAL_LONG_MESSAGE
    };

    private static final int MAX_PROGRESS = 100; // The maximum progress of
    // ProgressDialog
    private static final int REQ_RINGTONE_PICKER = 12345;

    private final Handler mHandler = new Handler(); // used to update the
    // progress
    private boolean mCancelled = false;
    private boolean mShowToasts = true;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog_layout);

        findViewById(R.id.activity_dialog_btn).setVisibility(View.GONE);
        ((ToggleButton) findViewById(R.id.showToasts)).setChecked(mShowToasts);
        Intent i = getIntent();
        if (null == i) {
            return;
        }
        int id = i.getIntExtra("dialogId", 0);
        showDialog(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_RINGTONE_PICKER:
                if (Activity.RESULT_CANCELED == resultCode) {
                    showToast("cancelled 'RingtonePickerAlertActivity'");
                } else {
                    Uri uri = data
                        .getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    showToast("ringtone choosed: uri=" + uri);
                }

                break;
        }
    }

    private void showToast(String message) {
        if (mShowToasts)
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_dialog_btn:
                showDialog(DIALOG_ALL_MESSAGE_DIALOGS);
                break;

            case R.id.list_dialog_btn:
                showDialog(DIALOG_ALL_LIST_DIALOGS);
                break;

            case R.id.progress_dialog_btn:
                showDialog(DIALOG_ALL_PROGRESS_DIALOGS);
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final Resources res = getResources();
        final String va_ok = getString(R.string.va_ok);
        final String va_cancel = getString(R.string.va_cancel);

        switch (id) {

            case DIALOG_ALL_MESSAGE_DIALOGS:
                // Create a list dialog
                return new AlertDialog.Builder(mContext)
                    .setTitle(R.string.all_text_dialogs)
                    .setItems(R.array.text_dialog_items,
                        new DialogInterface.OnClickListener() {
                            @SuppressWarnings("deprecation")
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                showDialog(IDS_ALL_MESSAGE_DIALOGS[which]);
                            }
                        }).create();

            case DIALOG_ALL_LIST_DIALOGS:
                // Create a list dialog
                return new AlertDialog.Builder(mContext)
                    .setTitle(R.string.all_list_dialogs)
                    .setItems(R.array.list_dialog_items,
                        new DialogInterface.OnClickListener() {
                            @SuppressWarnings("deprecation")
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                showDialog(IDS_ALL_LIST_DIALOGS[which]);
                            }
                        }).create();

            case DIALOG_ALL_PROGRESS_DIALOGS:
                // Create a list dialog
                return new AlertDialog.Builder(mContext)
                    .setTitle(R.string.all_progress_dialogs)
                    .setItems(R.array.progress_dialog_items,
                        new DialogInterface.OnClickListener() {
                            @SuppressWarnings("deprecation")
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                showDialog(IDS_ALL_PROGRESS_DIALOGS[which]);
                            }
                        }).create();

            // -----------------------------------------------------------------------------------

            case DIALOG_ACKNOWLEDGEMENT:
                // Create a short message (about 2 lines) dialog with 1 OK
                // button
                final String ack_title = getString(R.string.delete_duplicate_contacts);
                return new AlertDialog.Builder(mContext).setTitle(ack_title)
                    .setMessage(R.string.less_than_two)
                    .setPositiveButton(va_ok, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            showToast(va_ok + " " + ack_title);
                        }

                    }).setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface arg0) {
                            showToast(va_cancel + " " + ack_title);
                        }

                    }).create();

            case DIALOG_QUESTION:
                // Create a question (longer, about 3 lines) dialog with OK and
                // Cancel buttons
                final String question_title = getString(R.string.remove_account);
                return new AlertDialog.Builder(mContext)
                    .setTitle(question_title + "?")
                    .setMessage(R.string.remove_account_warning)
                    .setPositiveButton(question_title, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showToast(question_title);
                        }

                    })
                    .setNegativeButton("Turn on speed off More Info", new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showToast("More Info" + " " + question_title);
                        }
                    }).setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface arg0) {
                            showToast(va_cancel + " " + question_title);
                        }

                    }).create();

            case DIALOG_LEGAL_MESSAGE:
                // super long message with no button
                final String legal_title = getString(R.string.htc_legal);
                return new AlertDialog.Builder(mContext)
                    .setTitle(legal_title)
                    .setMessage(R.string.alert_dialog_long_message_content)
                    .setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface arg0) {
                            showToast(va_cancel + " " + legal_title);
                        }

                    }).create();

            case DIALOG_ONE_LINE:
                final String oneLine_title = getString(R.string.center_align);
                AlertDialog oneLine_dialog = new AlertDialog.Builder(mContext)
                    .setTitle(oneLine_title + " " + oneLine_title)
                    .setMessage(R.string.one_line_message)
                    .setPositiveButton(va_ok, null)
                    .setNegativeButton(
                        va_cancel + " " + va_ok + " " + va_ok + " " + va_ok,
                        null)
                    .setNeutralButton(
                        getString(R.string.neutral) + " " + va_ok + " "
                            + va_cancel + " " + va_ok + " " + va_cancel,
                        null)
                    .setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            showToast(va_cancel + " " + oneLine_title);
                        }

                    }).create();
                oneLine_dialog.setOnShowListener(new OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {
                        try {
                            View shadow = ((ViewGroup) ((AlertDialog) dialog)
                                .findViewById(android.R.id.content))
                                .getChildAt(0);
                            Method m = shadow.getClass().getMethod(
                                "setForcePortraitWidth", boolean.class);
                            m.invoke(shadow, true);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }

                });
                return oneLine_dialog;

            case DIALOG_CHECK_MESSAGE:
                // test check box feature, and lengthy title text (more then 2
                // lines)
                // PS. according to designer Potter, should show "..." at the
                // end
                final String checkMessageTitle = getString(R.string.demo_checkbox)
                    + " ";
                final AlertDialog.Builder chkMsgBuilder = new AlertDialog.Builder(
                    mContext)
                    .setTitle(
                        checkMessageTitle + checkMessageTitle
                            + checkMessageTitle + checkMessageTitle
                            + checkMessageTitle + checkMessageTitle)
                    .setMessage(R.string.checkbox_description)
                    .setPositiveButton(va_ok, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // do nothing
                        }

                    });
                return chkMsgBuilder.create();

            case DIALOG_CHECK_ONLY:
                // 301x
                final String demo_checkbox = getString(R.string.demo_checkbox);
                return new AlertDialog.Builder(mContext)
                    .setView(new EditText(this))
                    .setTitle(demo_checkbox + demo_checkbox + demo_checkbox + demo_checkbox + demo_checkbox + demo_checkbox + demo_checkbox)
                    .setPositiveButton(va_ok,
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // do nothing
                            }

                        }).create();

            case DIALOG_CHECK_MANY:
                // 301x
                final String demo_checkbox1 = getString(R.string.demo_checkbox);
                View view = LayoutInflater.from(mContext).inflate(R.layout.alert_dialog_text, null);
                HtcAlertDialog dialog1 = new HtcAlertDialog.Builder(mContext)
                        .setView(view)
                        .setTitle(demo_checkbox1 + demo_checkbox1 + demo_checkbox1 + demo_checkbox1 + demo_checkbox1 + demo_checkbox1 + demo_checkbox1)
                        .setCheckBox(getString(R.string.dont_ask), false, (CompoundButton.OnCheckedChangeListener)null, true)
                        .setPositiveButton(va_ok,
                                new OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        // do nothing
                                    }

                                }).create();
                dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                return dialog1;


            // ----------------------------------------------------------------------------

            case DIALOG_LIST:
                // Create a list dialog, 0 button
                return new AlertDialog.Builder(mContext)
                    .setTitle(R.string.list_dialog_title)
                    .setItems(R.array.list_dialog_items1,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String[] items = res
                                    .getStringArray(R.array.list_dialog_items1);
                                showToast("Your favorite animal is "
                                    + items[which]);
                            }
                        }).setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            showToast(va_cancel + " "
                                + getString(R.string.list_dialog_title));
                        }

                    }).create();

            case DIALOG_SINGLE_CHOICE:
                // Create a single choice dialog, 1 button
                return new AlertDialog.Builder(mContext)
                    .setTitle(R.string.single_choice_list_dialog_title)
                    .setSingleChoiceItems(R.array.single_choice_dialog_items2,
                        0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String[] items = res
                                    .getStringArray(R.array.single_choice_dialog_items2);
                                showToast("choosed: " + items[which]);
                                // don't forget to dismiss the dialog
                                // manually
                                dialog.dismiss();
                            }
                        }).setNeutralButton("More Info", null).create();

            case DIALOG_MULTIPLE_CHOICES:
                // init items
                final String[] multipleItems = res
                    .getStringArray(R.array.multiple_choice_dialog_items3);
                final boolean[] checkedItems = new boolean[multipleItems.length];
                final boolean[] lastChecked = Arrays.copyOf(checkedItems,
                    checkedItems.length);

                return new AlertDialog.Builder(mContext)
                    .setTitle(R.string.multiple_choice_list_dialog_title)
                    .setMultiChoiceItems(R.array.multiple_choice_dialog_items3,
                        checkedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which, boolean isChecked) {
                                // update temp data (multiple_checked)
                                showToast(multipleItems[which]
                                    + (isChecked ? " checked"
                                    : " not checked"));
                            }
                        })
                    .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                StringBuilder str = new StringBuilder();
                                SparseBooleanArray itemPos = ((AlertDialog) dialog)
                                    .getListView()
                                    .getCheckedItemPositions();
                                for (int i = 0; i < itemPos.size(); ++i) {
                                    int pos = itemPos.keyAt(i);
                                    boolean isChecked = itemPos.valueAt(i);
                                    if (isChecked) {
                                        str.append(pos).append(":")
                                            .append(multipleItems[pos])
                                            .append("\n");
                                    }
                                }
                                showToast("ok: \n" + str);

                                // save result
                                System.arraycopy(checkedItems, 0,
                                    lastChecked, 0, lastChecked.length);
                            }
                        })
                    .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                showToast(va_cancel
                                    + " "
                                    + getString(R.string.multiple_choice_list_dialog_title));
                                // restore selections
                                for (int i = 0; i < lastChecked.length; ++i) {
                                    checkedItems[i] = lastChecked[i];
                                    ((AlertDialog) dialog).getListView()
                                        .setItemChecked(i,
                                            lastChecked[i]);
                                }
                            }

                        }).setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            showToast(va_cancel
                                + " "
                                + getString(R.string.multiple_choice_list_dialog_title));
                            // restore selections
                            for (int i = 0; i < lastChecked.length; ++i) {
                                checkedItems[i] = lastChecked[i];
                                ((AlertDialog) dialog).getListView()
                                    .setItemChecked(i, lastChecked[i]);
                            }
                        }

                    }).create();

            case DIALOG_LONG_LIST:
                return new AlertDialog.Builder(mContext)
                    .setTitle(R.string.long_list)
                    .setItems(R.array.long_list_dialog_items, null)
                    .setPositiveButton(R.string.alert_dialog_ok, null)
                    .setNegativeButton(R.string.alert_dialog_cancel, null)
                    .setNeutralButton(R.string.neutral, null).create();

            case DIALOG_CHECK_LIST:
                CompoundButton.OnCheckedChangeListener listCheckedListener = new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                                                 boolean arg1) {
                        Log.d("henry", "onCheckedChanged=" + arg1);
                    }
                };
                final AlertDialog.Builder chkListBuilder = new AlertDialog.Builder(
                    mContext)
                    .setTitle(R.string.demo_checkbox)
                    .setItems(R.array.list_dialog_items1,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String[] items = res
                                    .getStringArray(R.array.list_dialog_items1);
                                showToast("Your favorite animal is "
                                    + items[which]);
                            }
                        })
                    .setPositiveButton(R.string.alert_dialog_ok,
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                // do nothing
                            }

                        });
                return chkListBuilder.create();

            case DIALOG_FONT_SELECTOR:
                OnClickListener fontSizeListener = new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                };
                final AlertDialog.Builder fontSelectorBuilder = new AlertDialog.Builder(
                    mContext)
                    .setTitle(R.string.font_size);
                return fontSelectorBuilder.create();

            // ----------------------------------------------------------------------------

            case DIALOG_HORIZONTAL_PROGRESS:
                // Create a progress dialog
                final ProgressDialog horizontalProgress = new ProgressDialog(
                    mContext);

                final Runnable runnable = new Runnable() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void run() {
                        mHandler.removeCallbacks(this);

                        if (horizontalProgress.getMax() > horizontalProgress
                            .getProgress() && !mCancelled) {
                            Log.d(TAG, "runnable: increment by 1");
                            horizontalProgress.incrementProgressBy(1);
                            mHandler.postDelayed(this, 10000);
                        } else {
                            Log.d(TAG, "runnable: complete. remove dialog");
                            showToast("Finish exporting contacts");
                            removeDialog(DIALOG_HORIZONTAL_PROGRESS);
                        }
                    }

                };

                horizontalProgress.setTitle(R.string.progress_dialog_title);
                horizontalProgress.setMessage(getString(R.string.export_contact));
                horizontalProgress
                    .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                horizontalProgress.setMax(MAX_PROGRESS);
                horizontalProgress.setProgress(0);
                horizontalProgress.setCancelable(false);
                horizontalProgress.setButton(DialogInterface.BUTTON_POSITIVE,
                    getString(R.string.run_in_background),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            showToast("Continue exporting contact data in background...");
                        }
                    });
                horizontalProgress.setButton(DialogInterface.BUTTON_NEGATIVE,
                    getText(R.string.alert_dialog_cancel),
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            showToast("cancel. Exporting contact data...");
                            mCancelled = true;
                            mHandler.removeCallbacks(runnable);
                            Log.d(TAG,
                                "cancel: remove callback and set progress to 0");
                            horizontalProgress.setProgress(0);
                        }
                    });
                horizontalProgress.setOnShowListener(new OnShowListener() {

                    @Override
                    public void onShow(DialogInterface arg0) {
                        mCancelled = false;
                        mHandler.post(runnable);
                        Log.d(TAG, "onShow: start runnable");
                    }

                });

                return horizontalProgress;

            case DIALOG_HORIZONTAL_NO_MESSAGE:
                // this case is used to demo the margins when no text message
                // set.
                final String horizontalNoMessage_title = getString(R.string.horizontal_progress_without_message);
                ProgressDialog mProgressDialogNoMessage = new ProgressDialog(
                    mContext);
                mProgressDialogNoMessage.setTitle(horizontalNoMessage_title);
                mProgressDialogNoMessage
                    .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialogNoMessage.setMax(MAX_PROGRESS);
                mProgressDialogNoMessage.setButton(DialogInterface.BUTTON_POSITIVE,
                    getString(com.htc.lib1.cc.R.string.va_hide),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            showToast(getString(R.string.va_hide) + " "
                                + horizontalNoMessage_title);
                        }
                    });
                mProgressDialogNoMessage.setButton(DialogInterface.BUTTON_NEGATIVE,
                    va_cancel, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            showToast(va_cancel + horizontalNoMessage_title);
                        }
                    });
                mProgressDialogNoMessage.incrementProgressBy(23);
                mProgressDialogNoMessage.setOnShowListener(new OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {
                    }

                });
                return mProgressDialogNoMessage;

            case DIALOG_HORIZONTAL_LONG_MESSAGE:
                final String horizontalLong_title = getString(R.string.horizontal_progress_with_long_message);
                ProgressDialog mProgressDialogLongMessage = new ProgressDialog(
                    mContext);
                mProgressDialogLongMessage
                    .setMessage(getString(R.string.ask_no_more_3));
                mProgressDialogLongMessage.setTitle(horizontalLong_title);
                mProgressDialogLongMessage
                    .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialogLongMessage.setMax(MAX_PROGRESS);
                mProgressDialogLongMessage.setButton(
                    DialogInterface.BUTTON_POSITIVE,
                    getString(R.string.va_hide),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            showToast("ok " + horizontalLong_title);
                        }
                    });
                mProgressDialogLongMessage.setButton(
                    DialogInterface.BUTTON_NEGATIVE,
                    getText(R.string.alert_dialog_cancel),
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            showToast(va_cancel + " " + horizontalLong_title);
                        }
                    });
                mProgressDialogLongMessage.setProgress(23);
                return mProgressDialogLongMessage;

            case DIALOG_SPINNER_PROGRESS:
                // create an interminable progress dialog
                ProgressDialog mSpinnerDialog = new ProgressDialog(mContext);
                mSpinnerDialog
                    .setMessage(getString(com.htc.lib1.cc.R.string.st_loading));
                mSpinnerDialog.setIndeterminate(true);
                mSpinnerDialog.setCancelable(true);
                mSpinnerDialog.setOnCancelListener(new OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        showToast(va_cancel + " "
                            + getString(com.htc.lib1.cc.R.string.st_loading));
                    }

                });
                return mSpinnerDialog;

            case DIALOG_SPINNER_LISTITEM:
                // create an interminable progress dialog
                final String listSpinner_title = getString(R.string.spinner_with_title);
                final ProgressDialog mListItemSpinnerDialog = new ProgressDialog(
                    mContext);
                mListItemSpinnerDialog.setTitle(listSpinner_title);
                mListItemSpinnerDialog
                    .setMessage(getString(R.string.titled_spinner_message));
                mListItemSpinnerDialog.setIndeterminate(true);
                mListItemSpinnerDialog.setCancelable(true);
                mListItemSpinnerDialog.setOnCancelListener(new OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        showToast(va_cancel + " " + listSpinner_title);
                    }

                });
                mListItemSpinnerDialog.setOnShowListener(new OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {
                        // following code is ONLY used to improve Emma Code
                        // Coverage
                        mListItemSpinnerDialog
                            .getButton(ProgressDialog.BUTTON_NEGATIVE);
                        mListItemSpinnerDialog.getListView();
                    }

                });
                return mListItemSpinnerDialog;
        }
        return null;
    }
}
