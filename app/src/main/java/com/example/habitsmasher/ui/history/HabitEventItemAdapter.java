package com.example.habitsmasher.ui.history;

import android.app.DownloadManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitsmasher.Habit;
import com.example.habitsmasher.HabitEvent;
import com.example.habitsmasher.HabitEventList;
import com.example.habitsmasher.ImageDatabaseHelper;
import com.example.habitsmasher.ItemAdapter;
import com.example.habitsmasher.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.firebase.firestore.core.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * The HabitEventItemAdapter class
 * Based on HabitItemAdapter, extension of FirestoreRecycler that shows habit events list with
 * live updates
 */
public class HabitEventItemAdapter extends ItemAdapter<HabitEvent, HabitEventItemAdapter.HabitEventViewHolder> {
    // Initialize variables
    private static String DATE_FORMAT = "dd/MM/yyyy";
    private Context _context;
    private static Habit _parentHabit;
    private static String _userId;
    private static HabitEventList _habitEvents;
    private static HabitEventListFragment _habitEventListFragment;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options the firestore entities
     */
    public HabitEventItemAdapter(@NonNull FirestoreRecyclerOptions<HabitEvent> options,
                                 String userId,
                                 HabitEventList habitEvents) {
        super(options);
        _userId = userId;
        _habitEvents = habitEvents;
    }

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options the firestore entities
     */
    public HabitEventItemAdapter(@NonNull ArrayList<FirestoreRecyclerOptions<HabitEvent>> options,
                                 String userId,
                                 HabitEventList habitEvents) {
        super(options);
        _userId = userId;
        _habitEvents = habitEvents;
    }


    @NonNull
    @Override
    public HabitEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate view based on HabitEventViewHolder
        _context = parent.getContext();
        View view = LayoutInflater.from(_context).inflate(R.layout.habit_event_row, parent, false);
        return new HabitEventViewHolder(view, _snapshots, _userId, _context);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitEventViewHolder holder, int position, @NonNull HabitEvent habitEvent) {
        // Create image database helper and fetch image
        ImageDatabaseHelper imageDatabaseHelper = new ImageDatabaseHelper();
        imageDatabaseHelper.fetchImagesFromDB(holder._habitEventImage,
                imageDatabaseHelper.getHabitEventStorageReference(
                        habitEvent.getUserID(),
                        habitEvent.getParentHabitID(),
                        habitEvent.getId()));

        // Set UI elements of habit event
        holder._habitEventDate.setText(new SimpleDateFormat(DATE_FORMAT).format(habitEvent.getDate()));
        holder._habitEventComment.setText(habitEvent.getComment());
    }

    /**
     * This class holds any necessary elements of the habit event on the front-end
     */
    public static class HabitEventViewHolder extends RecyclerView.ViewHolder {
        // List of UI elements
        private final TextView _habitEventDate;
        private final TextView _habitEventComment;
        private final ImageView _habitEventImage;

        /**
         * Default constructor
         * @param itemView (View): The habit event view
         */
        public HabitEventViewHolder(@NonNull View itemView,
                                    ObservableSnapshotArray<HabitEvent> _snapshots,
                                    String userId,
                                    Context context) {
            super(itemView);

            // Connect UI elements
            _habitEventDate = itemView.findViewById(R.id.habit_event_date);
            _habitEventComment = itemView.findViewById(R.id.habit_event_comment);
            _habitEventImage = itemView.findViewById(R.id.habit_event_image);
        }
    }

}
