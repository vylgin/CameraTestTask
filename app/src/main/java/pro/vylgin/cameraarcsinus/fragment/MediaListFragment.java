package pro.vylgin.cameraarcsinus.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import pro.vylgin.cameraarcsinus.R;
import pro.vylgin.cameraarcsinus.model.MediaContent;
import pro.vylgin.cameraarcsinus.utils.Utils;


public class MediaListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private AbsListView listView;
    private ListAdapter adapter;
    private int currentSpinnerPosition = Utils.ALL_MEDIAFILES_PISITION;

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

        Spinner spinner = (Spinner) view.findViewById(R.id.changeMediaContentSpinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.type_media_content_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            adapter = new ArrayAdapter<MediaContent.MediaItem>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, MediaContent.ITEMS);

            ((AdapterView<ListAdapter>) listView).setAdapter(adapter);

            if (MediaContent.ITEMS.isEmpty()) {
                setEmptyText(getActivity().getString(R.string.empty_media_list));
            }
        }
    }
}
