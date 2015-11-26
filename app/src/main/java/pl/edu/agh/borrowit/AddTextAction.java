package pl.edu.agh.borrowit;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by lukasz on 26.11.15.
 */
public class AddTextAction {


    public interface TextSendListener {
        void onSend(String phoneNumber);

        void onCancel();
    }

    public AddTextAction(final Context context, final TextSendListener listener) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_view);
        dialog.setTitle("Set headers");
        final EditText phoneNumber = (EditText) dialog.findViewById(R.id.phoneNumber);
        Button send = (Button) dialog.findViewById(R.id.OK);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = phoneNumber.getText().toString();

                if (number.length() == 0) {
                    Toast.makeText(context, "Enter valid number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listener != null)
                    listener.onSend(number);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onCancel();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
