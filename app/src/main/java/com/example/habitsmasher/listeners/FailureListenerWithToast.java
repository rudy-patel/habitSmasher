package com.example.habitsmasher.listeners;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
/**
 * This is the FailureListenerWithToast class
 * It implements the OnFailureListener
 * Its purpose is to perform the onFailure listener actions
 */
public class FailureListenerWithToast implements OnFailureListener{
    private Context _context;
    private String _logTag;
    private String _toastText;
    private String _logMsg;

    public FailureListenerWithToast(Context context, String logTag, String logMsg, String toastText) {
        _context = context;
        _logTag = logTag;
        _toastText = toastText;
        _logMsg = logMsg;
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.d(_logTag, _logMsg);
        try {
            Toast.makeText(_context, _toastText, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException error) {
            // Expected in unit test
        };
    }
}
