package space.selfenrichment.dailyselfie;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import space.selfenrichment.dailyselfie.lib.ImagesFS;

import static space.selfenrichment.dailyselfie.GridViewFragment.GRIDVIEW_FRAGMENT;
import static space.selfenrichment.dailyselfie.MainActivity.COMMAND_NAVTO;
import static space.selfenrichment.dailyselfie.lib.Defs.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FullImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FullImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FullImageFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IMAGE_FILENAME = "ARG_IMAGE_FILENAME";
    public static final String FULLIMAGE_FRAGMENT = "FULLIMAGE_FRAGMENT";

    private String mImageFilename;
    private ImageView mImageView;
    private OnFragmentInteractionListener mListener;

    public FullImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param image_filename Parameter 1.
     * @return A new instance of fragment FullImageFragment.
     */
    public static FullImageFragment newInstance(String image_filename) {
        Log.i(TAG, "newInstance of FullImageFragment with image_filename:" + image_filename);

        FullImageFragment fragment = new FullImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_FILENAME, image_filename);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "FullImageFragment onCreate");

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageFilename = getArguments().getString(ARG_IMAGE_FILENAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "FullImageFragment onCreateView");

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_full_image, container, false);

        mImageView = fragmentView.findViewById(R.id.imageView_full);
        if(mImageFilename != null) {
            Log.i(TAG, "FullImageFragment loading image to my view now");

            ImagesFS.loadImageToView(getActivity(), mImageView, mImageFilename);
        }

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullImageFragment.this.dismiss();
            }
        });

        return fragmentView;
    }

    public void dismiss() {
        Log.i(TAG, "FullImageFragment dismiss");

        if (mListener != null) {
            mListener.onFragmentInteraction(COMMAND_NAVTO, GRIDVIEW_FRAGMENT, mImageFilename);
        }
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
