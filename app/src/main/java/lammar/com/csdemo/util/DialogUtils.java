package lammar.com.csdemo.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

/**
 * Created by marcinlament on 01/02/2017.
 */

public class DialogUtils {

    public static DialogFragment showAlertDialog(FragmentActivity activity, int title, int message, int button){
        DialogFragment newFragment = AlertDialogFragment.newInstance(title, message, button);
        newFragment.show(activity.getSupportFragmentManager(), "dialog");
        return newFragment;
    }

    public static class AlertDialogFragment extends DialogFragment {

        public static AlertDialogFragment newInstance(int title, int message, int button) {
            AlertDialogFragment frag = new AlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            args.putInt("message", message);
            args.putInt("button", button);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            int title = getArguments().getInt("title");
            int message = getArguments().getInt("message");
            int button = getArguments().getInt("button");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            if(title > 0){
                builder.setTitle(title);
            }

            if(message > 0){
                builder.setMessage(message);
            }

            if(button > 0){
                builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }

            return builder.create();
        }
    }
}
