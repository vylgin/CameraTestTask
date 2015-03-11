package pro.vylgin.cameraarcsinus.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;

import pro.vylgin.cameraarcsinus.R;
import pro.vylgin.cameraarcsinus.model.MediaContent;
import pro.vylgin.cameraarcsinus.utils.Utils;


public class MediaListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String CURRENT_SPINNER_POSITION = "CURRENT_SPINNER_POSITION";

    private AbsListView listView;
    private int currentSpinnerPosition;

    public static MediaListFragment newInstance() {
        MediaListFragment fragment = new MediaListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public MediaListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mediaitem, container, false);

        listView = (AbsListView) view.findViewById(R.id.mediaListView);
        listView.setEmptyView(view.findViewById(R.id.emptyTextView));
        listView.setOnItemClickListener(this);
        updateMediaList();

        currentSpinnerPosition = getCurrentSpinnerPositionFromPreferences();

        Spinner spinner = (Spinner) view.findViewById(R.id.changeMediaContentSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.type_media_content_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(currentSpinnerPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSpinnerPosition = position;
                updateMediaList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveCurrentSpinnerPositionToPreferences();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String mediaFilePath = MediaContent.ITEMS.get(position).contentPath;
        Intent viewIntent = new Intent(Intent.ACTION_VIEW);
        File file = new File(mediaFilePath);

        if (mediaFilePath.contains(Utils.AUD)) {
            viewIntent.setDataAndType(Uri.fromFile(file), "audio/*");
        } else if (mediaFilePath.contains(Utils.VID)) {
            viewIntent.setDataAndType(Uri.fromFile(file), "video/*");
        }

        startActivity(Intent.createChooser(viewIntent, null));
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = listView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }


    public void updateMediaList() {
        Utils.updateMediaContent(currentSpinnerPosition);

        if (isAdded()) {
            ListAdapter adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, MediaContent.ITEMS);

            listView.setAdapter(adapter);

            if (MediaContent.ITEMS.isEmpty()) {
                setEmptyText(getActivity().getResources().getString(R.string.empty_media_list));
            }
        }
    }

    private void saveCurrentSpinnerPositionToPreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_SPINNER_POSITION, currentSpinnerPosition);
        editor.apply();
    }

    private int getCurrentSpinnerPositionFromPreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getInt(CURRENT_SPINNER_POSITION, Utils.ALL_MEDIAFILES_PISITION);
    }
}
