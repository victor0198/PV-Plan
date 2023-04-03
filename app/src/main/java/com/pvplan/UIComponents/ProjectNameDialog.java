package com.pvplan.UIComponents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.pvplan.R;

public class ProjectNameDialog extends AppCompatDialogFragment {

    private EditText projectNameEditText;
    private ProjectNameDialogListener listener;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_project_dialog, null);
        builder.setView(view)
                .setTitle("Project name")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String projectName = projectNameEditText.getText().toString();
                        listener.applyText(projectName);
                    }
                });
        projectNameEditText = view.findViewById(R.id.project_name_edit);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            listener = (ProjectNameDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException( context.toString() + "must implement project name dialog listener");
        }
    }

    public interface ProjectNameDialogListener {
        void applyText(String projectName);
    }
}
