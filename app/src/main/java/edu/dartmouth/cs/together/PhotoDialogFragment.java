package edu.dartmouth.cs.together;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoDialogFragment extends DialogFragment {

    public static final int DIALOG_ID_PHOTO_PICKER = 1;
    public static final int ID_PHOTO_PICKER_FROM_GALLERY = 1;
    // For photo picker selection:
    public static final int ID_PHOTO_PICKER_FROM_CAMERA = 0;
    private static final String DIALOG_ID_KEY = "dialog_id";

    public static PhotoDialogFragment newInstance(int dialog_id) {
        PhotoDialogFragment frag = new PhotoDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ID_KEY, dialog_id);
        frag.setArguments(args);
        return frag;
    }


//    public static PhotoDialogFragment newInstance( ) {
//        PhotoDialogFragment frag = new PhotoDialogFragment();
//        Bundle args = new Bundle();
//        frag.setArguments(args);
//        return frag;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Activity parent = getActivity();

        // Setup dialog appearance and onClick Listeners
        // Build picture picker dialog for choosing from camera or gallery
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setTitle(R.string.ui_profile_photo_picker_title);
        // Set up click listener, firing intents open camera
        DialogInterface.OnClickListener dlistener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Item is ID_PHOTO_PICKER_FROM_CAMERA
                // Call the onPhotoPickerItemSelected in the parent
                // activity, i.e., ameraControlActivity in this case
                ((ProfileActivity) parent)
                        .onPhotoPickerItemSelected(item);
            }
        };
        // Set the item/s to display and create the dialog
        builder.setItems(R.array.ui_profile_photo_picker_items, dlistener);
        return builder.create();

    }
}
