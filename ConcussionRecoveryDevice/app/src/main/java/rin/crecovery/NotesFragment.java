package rin.crecovery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotesFragInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment implements ActivityInteractionListener {

    private static final String FILEDIR = "";
    private static final String FILENAME = "notes.txt";

    private FragmentInteractionListener mFragListener;
    private NotesFragInteractionListener mListener;

    private EditText notes;

    public NotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotesFragment.
     */
    public static NotesFragment newInstance() {
        NotesFragment fragment = new NotesFragment();
        /*Bundle args = new Bundle();
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        notes = (EditText) view.findViewById(R.id.notes);
        if (IoUtils.isFileExisting(getContext(), FILEDIR, FILENAME))
            notes.setText(IoUtils.readStringFromFile(getContext(), FILEDIR, FILENAME));

        return view;
    }

    @Override
    public void onButtonPressed() {
        IoUtils.writeBytestoFile(getContext(), FILEDIR, FILENAME,
                notes.getText().toString().getBytes());
        mFragListener.onCreateSnackbar(notes, "Notes saved.");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NotesFragInteractionListener &&
                context instanceof FragmentInteractionListener) {
            mFragListener = (FragmentInteractionListener) context;
            mListener = (NotesFragInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NotesFragInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean bluetooth() {
        return false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface NotesFragInteractionListener {
    }
}
