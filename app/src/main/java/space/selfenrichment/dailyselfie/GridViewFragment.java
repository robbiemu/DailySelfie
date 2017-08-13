package space.selfenrichment.dailyselfie;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import space.selfenrichment.dailyselfie.lib.ImagesFS;

import static space.selfenrichment.dailyselfie.lib.Defs.*;
import static space.selfenrichment.dailyselfie.FullImageFragment.FULLIMAGE_FRAGMENT;
import static space.selfenrichment.dailyselfie.MainActivity.COMMAND_NAVTO;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GridViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GridViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GridViewFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IMAGE_FILENAME = "ARG_IMAGE_FILENAME";
    public static final String GRIDVIEW_FRAGMENT = "GRIDVIEW_FRAGMENT";

    private String mImageFilename;
    private ImagesAdapter mImageAdapter;
    private GridView mGridViewImage;


    private OnFragmentInteractionListener mListener;
    private List<String> mImages;

    public GridViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param imageFilename Parameter 1.
     * @return A new instance of fragment GridViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GridViewFragment newInstance(String imageFilename) {
        Log.i(TAG, "newInstance of GridViewFragment");

        GridViewFragment fragment = new GridViewFragment();

        if(imageFilename != null) {
            Bundle args = new Bundle();
            args.putString(ARG_IMAGE_FILENAME, imageFilename);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "GridViewFragment onCreate");

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageFilename = getArguments().getString(ARG_IMAGE_FILENAME); // we can scroll to focus
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "GridViewFragment onCreateView");

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_grid_view, container, false);

        mGridViewImage = (GridView) fragmentView.findViewById(R.id.gridView_images);

        mImages = new ArrayList<String>(Arrays.asList(ImagesFS.list(getActivity())));
        View.OnClickListener vOCL = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFullView((String) view.getTag(R.id.tag_filename));
            }
        };

        mImageAdapter = new ImagesAdapter(getActivity(), vOCL, fragmentView.getId(), mImages);

        mGridViewImage.setAdapter(mImageAdapter);

        return fragmentView;
    }

    public void onFullView(String filename) {
        if (mListener != null) {
            mListener.onFragmentInteraction(COMMAND_NAVTO, FULLIMAGE_FRAGMENT, filename);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void addFile(String fileUriString) {

        Log.i(TAG, "file from camera shot ready for adding to mImages. filename: " + fileUriString);

        mImages.add(ImagesFS.getName(fileUriString));

        mImageAdapter.notifyDataSetChanged();
    }

    public void clear() {
        mImageAdapter.clear();
        mImageAdapter.notifyDataSetChanged();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Byte command, String fragment_identifier, String filename);
    }
}
